package com.tntkhang.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.INAPP
import com.android.billingclient.api.BillingClient.BillingResponseCode.*

/**
 * https://developer.android.com/google/play/billing/billing_library_overview
 * https://github.com/googlesamples/android-play-billing/blob/9e7e373ae2414af9d5b170e000a2ba9deb911944/TrivialDriveKotlin/app/src/main/java/com/kotlin/trivialdrive/billingrepo/BillingRepository.kt#L662
 */

class BillingHelper(
    private val activity: Activity,
    private val donateClientListener: DonateClientListener,
    private val isCheckLicense: Boolean = false
) : PurchasesUpdatedListener, BillingClientStateListener {

     interface DonateClientListener {
         fun skuDetailsResult(skuDetailsList: List<SkuDetails>) = Unit
         fun acknowledgedPurchase(isBought: Boolean = false) = Unit
    }

    private lateinit var billingClient: BillingClient

    fun setupBillingClient() {
        billingClient = BillingClient
            .newBuilder(activity)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        startConnect()
    }

    private fun startConnect() {
        billingClient.startConnection(this)
    }

    fun querySkuDetails() {
        val skuList = listOf("gts_watch_face")
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                OK -> if (skuDetailsList.orEmpty().isNotEmpty()) {
                    skuDetailsList.sortBy { it.priceAmountMicros }
                    donateClientListener.skuDetailsResult(skuDetailsList)
                }
                else -> Log.d("tntkhang","querySkuDetails response -> ${billingResult.responseCode.billingCodeName()}")
            }
        }
    }

    fun makePurchase(skuDetails: SkuDetails) {
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    override fun onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
        println("BILLING | onBillingServiceDisconnected | DISCONNECTED")

        startConnect()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        if (billingResult?.responseCode == OK) {
            if (isCheckLicense) {
                val isAcknowledged = checkHasPurchaseAcknowledged()
                donateClientListener.acknowledgedPurchase(isAcknowledged)
            } else {
                querySkuDetails()
            }
        } else {
            if (isCheckLicense) {
                donateClientListener.acknowledgedPurchase(false)
            }
            Log.d("tntkhang","onBillingSetupFinished -> ${billingResult?.responseCode?.billingCodeName()}")
        }
    }

    /**
     * Handle buy error before with acknowledged = false
     */
    private fun handleConsumablePurchasesAsync(consumables: List<Purchase>) {
        consumables.forEach {
            val params =
                ConsumeParams.newBuilder().setPurchaseToken(it.purchaseToken).build()
            billingClient.consumeAsync(params) { billingResult, _ ->
                when (billingResult.responseCode) {
                    OK -> Log.d("tntkhang", "Consumed the old purchase that hasn't already been acknowledged")
                    else -> Log.d("tntkhang","Error consume the old purchase that hasn't already been acknowledged -> ${billingResult.responseCode.billingCodeName()}")
                }
            }
        }
    }

    /**
     * If you do not acknowledge a purchase, the Google Play Store will provide a refund to the
     * users within a few days of the transaction. Therefore you have to implement
     * [BillingClient.acknowledgePurchase] inside your app.
     */
    private fun acknowledgeNonConsumablePurchasesAsync(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            when (billingResult.responseCode) {
                OK -> donateClientListener.acknowledgedPurchase(true)
                else -> Log.e("tntkhang", "acknowledgeNonConsumablePurchasesAsync() $billingResult.responseCode")
            }
        }
    }

    private fun checkHasPurchaseAcknowledged(): Boolean {
        val queryPurchases = billingClient.queryPurchases(INAPP)
        if (queryPurchases.responseCode == OK) {
            queryPurchases.purchasesList?.forEach {
                if (it.isAcknowledged) {
                    return true
                } else if (it.purchaseState != Purchase.PurchaseState.PENDING) { // Remove a purchase haven't been acknowledged before
                    handleConsumablePurchasesAsync(listOf(it))
                }
            }
        }
        return false
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if (billingResult != null) {
            when (billingResult.responseCode) {
                OK -> purchases?.forEach {
                    if (it.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        // Acknowledge the purchase if it hasn't already been acknowledged.
                        if (!it.isAcknowledged) {
                            acknowledgeNonConsumablePurchasesAsync(it)
                        } else {
                            donateClientListener.acknowledgedPurchase(true)
                        }
                    }
                }

                USER_CANCELED -> Log.d("tntkhang","onPurchasesUpdated() user canceled")

                ITEM_ALREADY_OWNED -> {
                    val isAcknowledged = checkHasPurchaseAcknowledged()
                    donateClientListener.acknowledgedPurchase(isAcknowledged)
                }

                else -> Log.d("tntkhang", "onPurchasesUpdated() $billingResult.responseCode")
            }
        }
    }
}

class DonationException(
    action: String,
    responseCode: Int
) : Exception("$action unsuccessful - responseCode = ${responseCode.billingCodeName()}")

private fun Int.billingCodeName(): String = when (this) {
    FEATURE_NOT_SUPPORTED -> "FEATURE_NOT_SUPPORTED"
    SERVICE_DISCONNECTED -> "SERVICE_DISCONNECTED"
    OK -> "OK"
    USER_CANCELED -> "USER_CANCELED"
    SERVICE_UNAVAILABLE -> "SERVICE_UNAVAILABLE"
    BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
    ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
    DEVELOPER_ERROR -> "DEVELOPER_ERROR"
    ERROR -> "ERROR"
    ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
    ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
    else -> "Not Know??"
}