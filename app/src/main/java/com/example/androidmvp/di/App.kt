package com.example.androidmvp.di

import android.app.Application
import com.example.androidmvp.di.modules.AppModule
import moxy.MvpFacade
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router

class App : Application() {
    private var cicerone: Cicerone<Router>? = null
    var instance: App? = null

    override fun onCreate() {
        super.onCreate()

        MvpFacade.init()
        instance = this
        cicerone = Cicerone.create()

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    fun getNavigatorHolder(): NavigatorHolder? {
        return cicerone?.navigatorHolder
    }

    fun getRouter(): Router? {
        return cicerone?.router
    }


    companion object {
        lateinit var appComponent: AppComponent
    }
}
