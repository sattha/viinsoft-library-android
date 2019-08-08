package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.Result
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler] from Complete [Observable].
 */
abstract class CompletableUseCase<P>(scheduler: SchedulerProvider) : RxUseCase<P, Unit>(scheduler){

    private var disposable: Disposable? = null

    /**
     * An Completable observable use-case to be executed.
     *
     * @param parameters [P] object to use in the use case
     * @return [Completable] source to be executed.
     */
    protected abstract fun execute(parameters: P): Completable

    override fun invoke(parameters: P, result: MutableLiveData<Result<Unit>>) {

        disposable = execute(parameters)
            .doOnSubscribe { result.postValue(Result.loading()) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({
                result.postValue(Result.success(null))
            }, { e ->
                result.postValue(Result.error(e as Exception))
            })
    }

    override fun invokeSync(parameters: P): Result<Unit> {
        val e = execute(parameters).blockingGet()
        return if (e == null) {
            Result.success(null)
        } else {
            Result.error((e as Exception?)!!)
        }
    }

    override fun getDisposable(): Disposable? {
        return disposable
    }
}
