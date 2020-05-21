package com.example.androidmvp.di

import android.app.Application
import com.example.androidmvp.di.modules.AppModule
import moxy.MvpFacade

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        MvpFacade.init()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}
