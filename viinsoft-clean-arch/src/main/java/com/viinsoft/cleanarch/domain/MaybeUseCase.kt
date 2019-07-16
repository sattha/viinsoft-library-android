package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.Result
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler] from Maybe [Observable].
 */
abstract class MaybeUseCase<P, R>(scheduler: SchedulerProvider) : RxUseCase<P, R>(scheduler), Mediator<R> {

    private var disposable: Disposable? = null
    private var observable: MediatorLiveData<Result<R>>? = null

    /**
     * An Maybe observable use-case to be executed.
     *
     * @param parameters [P] object to use in the use case
     * @return [<] source to be executed.
     */
    protected abstract fun execute(parameters: P?): Maybe<R>

    override fun invoke(parameters: P?, result: MutableLiveData<Result<R>>) {

        if (observable != null) observable!!.postValue(Result.loading(null))
        result.postValue(Result.loading(null))

        disposable = execute(parameters)
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.io())
            .subscribe({ r ->
                if (observable != null) observable!!.postValue(Result.success(r))
                result.postValue(Result.success(r))
            }, { e ->
                if (observable != null) observable!!.postValue(Result.error(e as Exception, null))
                result.postValue(Result.error(e as Exception, null))
            }, {
                if (observable != null) observable!!.postValue(Result.success(null))
                result.postValue(Result.success(null))
            })
    }


    override fun invokeSync(parameters: P?): Result<R> {
        return try {
            Result.success(execute(parameters).blockingGet())
        } catch (e: RuntimeException) {
            Result.error(e, null)
        }

    }

    override fun getDisposable(): Disposable? {
        return disposable
    }

    override fun observe(observable: MediatorLiveData<Result<R>>) {
        this.observable = observable
    }
}
