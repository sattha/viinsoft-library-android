package com.viinsoft.cleanarch.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.viinsoft.cleanarch.model.UseCaseState

fun <X, Y> LiveData<X>.map(block: (X) -> (Y)): LiveData<Y> {
    return Transformations.map(this, block)
}

fun <X, Y> LiveData<X>.switchMap(block: (X) -> (LiveData<Y>)): LiveData<Y> {
    return Transformations.switchMap(this, block)
}

fun <T> LiveData<UseCaseState<T>>.onLoad(): LiveData<Boolean> {

    val filteredLiveData = MediatorLiveData<Boolean>()

    filteredLiveData.addSource(this) {
        val isLoading = when (it) {
            is UseCaseState.LoadContent -> true
            is UseCaseState.Complete -> false
            is UseCaseState.Error -> false
        }
        filteredLiveData.value = isLoading
    }

    return filteredLiveData
}

fun <T> LiveData<UseCaseState<T>>.onSuccess(): LiveData<T> {

    val filteredLiveData = MediatorLiveData<T>()

    filteredLiveData.addSource(this) {
        if (it is UseCaseState.Complete) {
            filteredLiveData.value = it.value
        }
    }

    return filteredLiveData
}

fun <T> LiveData<UseCaseState<T>>.onSuccessWhen(block: (T) -> Boolean): LiveData<T> {

    val filteredLiveData = MediatorLiveData<T>()

    filteredLiveData.addSource(this) {
        if (it is UseCaseState.Complete)
            if (block.invoke(it.value))
                filteredLiveData.value = it.value
    }

    return filteredLiveData
}

fun <T> LiveData<UseCaseState<T>>.onFailure(): LiveData<Exception> {

    val filteredLiveData = MediatorLiveData<Exception>()

    filteredLiveData.addSource(this) {

        if (it is UseCaseState.Error)
            filteredLiveData.value = it.value as Exception?
    }

    return filteredLiveData
}

fun <T> LiveData<UseCaseState<T>>.onFailureWhen(block: (Exception?) -> Boolean): LiveData<Exception> {

    val filteredLiveData = MediatorLiveData<Exception>()

    filteredLiveData.addSource(this) {

        if (it is UseCaseState.Error)
            if (block.invoke(it.value as Exception?))
                filteredLiveData.value = it.value
    }

    return filteredLiveData
}