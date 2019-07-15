package com.viinsoft.cleanarch.model

import java.util.Arrays

/**
 * Base result for all use-case
 * @param <R></R>
 * */
open class Result<R> private constructor() {

    var isLoading = false
        private set

    var isSuccess = false
        private set

    var data: R? = null
        private set

    var exception: Exception? = null
        private set


    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Result<*>)
            return false
        return if (other === this) true else this.data === other.data
                && this.isLoading == other.isLoading
                && this.isSuccess == other.isSuccess
                && this.exception === other.exception
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(arrayOf(data, exception, isLoading, isSuccess))
    }

    companion object {

        fun <T> success(data: T?): Result<T> {
            val result = Result<T>()
            result.data = data
            result.exception = null
            result.isLoading = false
            result.isSuccess = true
            return result
        }

        fun <T> error(exception: Exception, nullData: T?): Result<T> {
            val result = Result<T>()
            result.data = null
            result.exception = exception
            result.isLoading = false
            result.isSuccess = false
            return result
        }

        fun <T> loading(nullData: T?): Result<T> {
            val result = Result<T>()
            result.data = null
            result.exception = null
            result.isLoading = true
            result.isSuccess = false
            return result
        }
    }
}
