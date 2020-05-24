package com.example.androidmvp.di

import com.example.androidmvp.ContainerActivity
import com.example.androidmvp.di.modules.ContainerModule
import com.example.androidmvp.di.scopes.ContainerScope
import dagger.Subcomponent

@ContainerScope
@Subcomponent(modules = [ContainerModule::class])
interface ContainerComponent {
    fun inject(conteinerActivity: ContainerActivity)
}