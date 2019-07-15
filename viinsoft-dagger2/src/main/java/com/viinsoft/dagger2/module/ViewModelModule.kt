package com.viinsoft.dagger2.module

import androidx.lifecycle.ViewModelProvider
import com.viinsoft.dagger2.helper.DaggerViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun daggerViewModelProvider(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}
