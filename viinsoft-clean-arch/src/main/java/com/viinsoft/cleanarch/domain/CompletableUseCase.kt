package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.UseCaseState
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler] from Complete [Observable].
 */
abstract class CompletableUseCase<P>(scheduler: SchedulerProvider) : RxUseCase<P, Unit>(scheduler) {

    private var disposable: Disposable? = null

    /**
     * An Completable observable use-case to be executed.
     *
     * @param parameters [P] object to use in the use case
     * @return [Completable] source to be executed.
     */
    protected abstract fun execute(parameters: P): Completable

    override fun invoke(parameters: P, result: MutableLiveData<UseCaseState<Unit>>) {

        disposable = execute(parameters)
            .doOnSubscribe { result.postValue(UseCaseState.LoadContent) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({
                result.postValue(UseCaseState.Complete(Unit))
            }, { e ->
                result.postValue(UseCaseState.Error(e))
            })
    }

    override fun invoke(parameters: P, result: (UseCaseState<Unit>) -> Unit) {

        disposable = execute(parameters)
            .doOnSubscribe { result.invoke(UseCaseState.LoadContent) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({
                result.invoke(UseCaseState.Complete(Unit))
            }, { e ->
                result.invoke(UseCaseState.Error(e))
            })
    }

    override fun invokeSync(parameters: P): UseCaseState<Unit> {
        val e = execute(parameters).blockingGet()
        return if (e == null) {
            UseCaseState.Complete(Unit)
        } else {
            UseCaseState.Error(e)
        }
    }

    override fun getDisposable(): Disposable? {
        return disposable
    }
}
