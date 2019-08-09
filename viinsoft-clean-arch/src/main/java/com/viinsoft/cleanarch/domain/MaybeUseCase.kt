package com.viinsoft.cleanarch.domain

import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.UseCaseState
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler] from Maybe [Observable].
 */
abstract class MaybeUseCase<P, R>(scheduler: SchedulerProvider) : RxUseCase<P, R?>(scheduler) {

    private var disposable: Disposable? = null

    /**
     * An Maybe observable use-case to be executed.
     *
     * @param parameters [P] object to use in the use case
     * @return [<] source to be executed.
     */
    protected abstract fun execute(parameters: P): Maybe<R>

    override fun invoke(parameters: P, result: MutableLiveData<UseCaseState<R?>>) {

        disposable = execute(parameters)
            .doOnSubscribe { result.postValue(UseCaseState.LoadContent) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.io())
            .subscribe({ r ->
                result.postValue(UseCaseState.Complete(r))
            }, { e ->
                result.postValue(UseCaseState.Error(e))
            }, {
                result.postValue(UseCaseState.Complete(null))
            })
    }

    override fun invoke(parameters: P, result: (UseCaseState<R?>) -> Unit) {

        disposable = execute(parameters)
            .doOnSubscribe { result.invoke(UseCaseState.LoadContent) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({
                result.invoke(UseCaseState.Complete(it))
            }, { e ->
                result.invoke(UseCaseState.Error(e))
            }, {
                result.invoke(UseCaseState.Complete(null))
            })
    }


    override fun invokeSync(parameters: P): UseCaseState<R?> {
        return try {
            UseCaseState.Complete(execute(parameters).blockingGet())
        } catch (e: RuntimeException) {
            UseCaseState.Error(e)
        }

    }

    override fun getDisposable(): Disposable? {
        return disposable
    }
}
