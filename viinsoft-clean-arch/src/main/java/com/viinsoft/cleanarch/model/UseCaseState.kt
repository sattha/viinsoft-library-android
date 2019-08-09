package com.viinsoft.cleanarch.model

/**
 * Base result for all use-case
 * @param <R></R>
 * */
sealed class UseCaseState<out T> {

    object LoadContent : UseCaseState<Nothing>()

    data class Complete<T>(val value: T) : UseCaseState<T>()

    data class Error(val value: Throwable?) : UseCaseState<Nothing>()
}
