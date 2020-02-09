package com.tntkhang.coronavirus

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.tntkhang.coronavirus.utils.Constants
import khangtran.preferenceshelper.PrefHelper

public class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        PrefHelper.initHelper(this)

        val isProVersion = PrefHelper.getBooleanVal(Constants.IS_PRO_ACTIVATED, false)
        Log.i("bipface", "isPro: $isProVersion")
        if (!isProVersion) {
            MobileAds.initialize(this) {}
            // Nokia 8.1 Mac AAVN
            AdRequest.Builder().addTestDevice("C72505E5DD1821541A61EB838CF7D586")

            AdRequest.Builder().addTestDevice("877C069F2EE066B70666572FF97122EA")

            // Pixel 3A XL
            AdRequest.Builder().addTestDevice("6548D6DDDB92B52AE9605169493FAD18")
            // Nokia 8.1
            AdRequest.Builder().addTestDevice("EEF3CB81020A4F6DB8F05955754EA26D")
        }
    }

}