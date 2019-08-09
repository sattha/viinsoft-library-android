package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.UseCaseState
import io.reactivex.disposables.Disposable

/**
 * Allow use case to be observable from other LiveData
 */
abstract class RxUseCase<P, R>(val scheduler: SchedulerProvider) {

    /**
     * provide Disposable object for stopping the execution of use case, you may need this for
     * using with [io.reactivex.disposables.CompositeDisposable].
     *
     * @return Represents a disposable resource.
     */
    abstract fun getDisposable(): Disposable?

    /**
     * execute the use case asynchronously.
     *
     * @param parameters additional parameters for using in the execute function's use case.
     * @param result     callback MutableLiveData<Result></Result><R>> post value back to the view.
    </R> */
    abstract fun invoke(parameters: P, result: MutableLiveData<UseCaseState<R>>)

    /**
     * execute the use case asynchronously.
     *
     * @param parameters additional parameters for using in the execute function's use case.
     * @param result     callback without live data
    </R> */
    abstract fun invoke(parameters: P, result: (UseCaseState<R>) -> Unit)

    /**
     * execute the use case synchronously.
     *
     * @param parameters additional parameters for using in the execute function's use case.
     * @return [<] value.
     */
    abstract fun invokeSync(parameters: P): UseCaseState<R>

    /**
     * Cancel the execution of the use case.
     */
    fun cancel() {
        val disposable = getDisposable()
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
