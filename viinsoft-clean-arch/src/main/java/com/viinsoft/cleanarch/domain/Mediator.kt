package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MediatorLiveData
import com.viinsoft.cleanarch.model.Result

interface Mediator<R> {

    /**
     * Allow the implementor to observe on other `LiveData`.
     *
     * @return an object that can observe other `LiveData` objects and react on.
     */
    fun observe(observable: MediatorLiveData<Result<R>>)
}
