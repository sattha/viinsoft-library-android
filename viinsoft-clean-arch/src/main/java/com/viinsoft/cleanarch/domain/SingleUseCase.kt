package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.Result
import io.reactivex.Single
import io.reactivex.disposables.Disposable

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler] from Single [Observable].
 */
abstract class SingleUseCase<P, R>(scheduler: SchedulerProvider) : RxUseCase<P, R>(scheduler) {

    private var disposable: Disposable? = null

    /**
     * A Single observable use-case to be executed.
     *
     * @param parameters [P] object to use in the use case
     * @return [<] source to be executed.
     */
    protected abstract fun execute(parameters: P?): Single<R>

    override fun invoke(parameters: P?, result: MutableLiveData<Result<R>>) {

        disposable = execute(parameters)
            .doOnSubscribe { result.postValue(Result.loading()) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({ data ->
                result.postValue(Result.success(data))
            }, { e ->
                result.postValue(Result.error(e as Exception))
            })
    }

    override fun invokeSync(parameters: P?): Result<R> {
        return try {
            Result.success(execute(parameters).blockingGet())
        } catch (e: RuntimeException) {
            Result.error(e)
        }
    }

    override fun getDisposable(): Disposable? = disposable
}
