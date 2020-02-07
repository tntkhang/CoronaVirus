package com.tntkhang.coronavirus

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.billingclient.api.SkuDetails
import com.tntkhang.billing.BillingHelper
import com.tntkhang.coronavirus.utils.Constants
import khangtran.preferenceshelper.PrefHelper

class MainActivity : AppCompatActivity(), BillingHelper.DonateClientListener  {

    private lateinit var billingHelper: BillingHelper
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_stats, R.id.navigation_map, R.id.navigation_chart, R.id.navigation_news, R.id.navigation_info
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        initBilling()
    }

    private fun initBilling() {
        billingHelper = BillingHelper(this, this, true)
        billingHelper.setupBillingClient()
    }

    override fun skuDetailsResult(skuDetailsList: List<SkuDetails>) {
        super.skuDetailsResult(skuDetailsList)
        if (skuDetailsList.isNotEmpty()) {
            billingHelper.makePurchase(skuDetailsList[0])
        }
    }

    override fun acknowledgedPurchase(isBought: Boolean) {
        super.acknowledgedPurchase(isBought)
        if (isBought) {
            menu?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_diamond)
            menu?.getItem(0)?.title = getString(R.string.pro_version)
            PrefHelper.setVal(Constants.IS_PRO_ACTIVATED, true)
        }
    }

    private fun onClickGetProVersion() {
        val isProVersion = PrefHelper.getBooleanVal(Constants.IS_PRO_ACTIVATED, false)
        if (!isProVersion) {
            billingHelper.querySkuDetails()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.remove_ads) {
            onClickGetProVersion()
        }
        return super.onOptionsItemSelected(item)
    }
}
