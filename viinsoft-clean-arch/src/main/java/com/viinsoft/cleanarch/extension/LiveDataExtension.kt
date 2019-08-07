package com.viinsoft.cleanarch.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.viinsoft.cleanarch.model.Result

fun <X, Y> LiveData<X>.map(block: (X) -> (Y)): LiveData<Y> {
    return Transformations.map(this, block)
}

fun <X, Y> LiveData<X>.switchMap(block: (X) -> (LiveData<Y>)): LiveData<Y> {
    return Transformations.switchMap(this, block)
}

fun <T> LiveData<Result<T>>.onLoad(): LiveData<Boolean> {

    val filteredLiveData = MediatorLiveData<Boolean>()

    filteredLiveData.addSource(this) {
        filteredLiveData.value = it.isLoading
    }

    return filteredLiveData
}

fun <T> LiveData<Result<T>>.onSuccess(): LiveData<T> {

    val filteredLiveData = MediatorLiveData<T>()

    filteredLiveData.addSource(this) {
        if (it.isLoading)
            return@addSource

        if (it.isSuccess) {
            filteredLiveData.value = it.data
        }
    }

    return filteredLiveData
}

fun <T> LiveData<Result<T>>.onSuccessWhen(block: (T) -> Boolean): LiveData<T> {

    val filteredLiveData = MediatorLiveData<T>()

    filteredLiveData.addSource(this) {
        if (it.isLoading)
            return@addSource

        if (it.isSuccess) {
            it.data?.apply {
                if (block.invoke(this))
                    filteredLiveData.value = it.data
            }
        }
    }

    return filteredLiveData
}

fun <T> LiveData<Result<T>>.onFailure(): LiveData<Exception> {

    val filteredLiveData = MediatorLiveData<Exception>()

    filteredLiveData.addSource(this) {
        if (it.isLoading)
            return@addSource

        if (!it.isSuccess)
            filteredLiveData.value = it.exception
    }

    return filteredLiveData
}

fun <T> LiveData<Result<T>>.onFailureWhen(block: (Exception) -> Boolean): LiveData<Exception> {

    val filteredLiveData = MediatorLiveData<Exception>()

    filteredLiveData.addSource(this) {
        if (it.isLoading)
            return@addSource

        if (!it.isSuccess) {
            it.exception
                ?.let { ex -> block.invoke(ex) }
                ?.also { isInterested -> if (isInterested) filteredLiveData.value = it.exception }
        }
    }

    return filteredLiveData
}

fun <T> LiveData<Result<T>>.observe(
    onLoadChange: MediatorLiveData<Boolean>?,
    onSuccess: MediatorLiveData<T>?,
    onFailure: MediatorLiveData<Exception>?
) {

    onLoadChange?.addSource(this) {
        onLoadChange.value = it.isLoading
    }

    onSuccess?.addSource(this) {
        if (it.isLoading)
            return@addSource

        if (it.isSuccess)
            onSuccess.value = it.data
    }

    onFailure?.addSource(this) {
        if (it.isLoading)
            return@addSource

        if (!it.isLoading)
            onFailure.value = it.exception
    }
}