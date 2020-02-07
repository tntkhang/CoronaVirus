package com.tntkhang.coronavirus

import android.app.Application
import khangtran.preferenceshelper.PrefHelper

public class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        PrefHelper.initHelper(applicationContext)
    }

}