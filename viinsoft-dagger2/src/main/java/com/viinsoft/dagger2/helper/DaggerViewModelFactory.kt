package com.viinsoft.dagger2.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import javax.inject.Inject
import javax.inject.Provider

class DaggerViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        for ((key, value) in creators) {
            if (modelClass.isAssignableFrom(key)) {
                try {
                    return value.get() as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }

            }
        }
        throw IllegalArgumentException("unknown model class $modelClass")
    }
}
