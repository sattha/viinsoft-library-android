package com.viinsoft.cleanarch.domain


import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.UseCaseState
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Executes business logic synchronously or asynchronously using a [Scheduler] from [Observable].
 */
abstract class ObservableUseCase<P, R>(scheduler: SchedulerProvider) : RxUseCase<P, R>(scheduler) {

    private var disposable: Disposable? = null

    /**
     * An observable use-case to be executed.
     *
     * @param parameters [P] object to use in the use case
     * @return [<] source to be executed.
     */
    protected abstract fun execute(parameters: P): Observable<R>

    override fun invoke(parameters: P, result: MutableLiveData<UseCaseState<R>>) {

        disposable = execute(parameters)
            .doOnSubscribe { result.postValue(UseCaseState.LoadContent) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({ r ->
                result.postValue(UseCaseState.Complete(r))
            }, { e ->
                result.postValue(UseCaseState.Error(e))
            })

    }

    override fun invoke(parameters: P, result: (UseCaseState<R>) -> Unit) {

        disposable = execute(parameters)
            .doOnSubscribe { result.invoke(UseCaseState.LoadContent) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({ r ->
                result.invoke(UseCaseState.Complete(r))
            }, { e ->
                result.invoke(UseCaseState.Error(e))
            })
    }


    override fun invokeSync(parameters: P): UseCaseState<R> {
        return try {
            UseCaseState.Complete(execute(parameters).blockingFirst())
        } catch (e: NoSuchElementException) {
            UseCaseState.Error(e)
        } catch (e: Exception) {
            UseCaseState.Error(e)
        }

    }

    override fun getDisposable(): Disposable? = disposable
}
