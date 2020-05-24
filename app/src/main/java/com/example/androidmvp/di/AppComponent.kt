package com.example.androidmvp.di

import android.app.Activity
import android.content.Context
import com.example.androidmvp.AuthActivity
import com.example.androidmvp.ContainerActivity
import com.example.androidmvp.MainActivity
import com.example.androidmvp.SignUpActivity
import com.example.androidmvp.di.modules.AppModule
import com.example.androidmvp.di.modules.FBModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, FBModule::class])
interface AppComponent {

    fun getContext(): Context
    fun inject(activity: AuthActivity)
    fun inject(activity: SignUpActivity)
    fun inject(activity: MainActivity)
    fun provideContainer(): ContainerActivity

    @Component.Builder
    interface Builder {
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
}
