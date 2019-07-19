package com.viinsoft.cleanarch.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> LiveData<T>.filter(block: (T) -> Boolean): LiveData<T> {
    val filteredLiveData = MediatorLiveData<T>()

    filteredLiveData.addSource(this) {
        it?.let {
            if (block.invoke(it))
                filteredLiveData.value = it
        }
    }

    return filteredLiveData
}