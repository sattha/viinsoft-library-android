package com.viinsoft.cleanarch.domain


import androidx.lifecycle.MutableLiveData
import com.viinsoft.cleanarch.helper.SchedulerProvider
import com.viinsoft.cleanarch.model.Result
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
    protected abstract fun execute(parameters: P?): Observable<R>

    override fun invoke(parameters: P?, result: MutableLiveData<Result<R>>) {

        disposable = execute(parameters)
            .doOnSubscribe { result.postValue(Result.loading()) }
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe({ r ->
                result.postValue(Result.success(r))
            }, { e ->
                result.postValue(Result.error(e as Exception))
            })

    }

    override fun invokeSync(parameters: P?): Result<R> {
        return try {
            Result.success(execute(parameters).blockingFirst())
        } catch (e: NoSuchElementException) {
            // if blockingFirst() it emits no items
            Result.error(e)
        } catch (e: Exception) {
            Result.error(e)
        }

    }

    override fun getDisposable(): Disposable? = disposable
}
