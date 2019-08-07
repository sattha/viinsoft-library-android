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
    onLoadChange: (Boolean) -> Unit,
    onSuccess: (T?) -> Unit,
    onFailure: (Exception) -> Unit
) {

    val observe = MediatorLiveData<Result<T>>()

    observe.addSource(this) {
        when {
            it.isLoading -> onLoadChange(it.isLoading)
            it.isSuccess -> {
                onSuccess(it.data)
                onLoadChange(it.isLoading)
            }
            else -> {
                onFailure(it.exception ?: Exception("no exception define (default from extension"))
                onLoadChange(it.isLoading)
            }
        }
    }
}