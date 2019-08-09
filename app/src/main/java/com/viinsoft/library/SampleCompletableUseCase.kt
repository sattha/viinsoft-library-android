package com.viinsoft.library

import com.viinsoft.cleanarch.domain.CompletableUseCase
import com.viinsoft.cleanarch.helper.SchedulerProvider
import io.reactivex.Completable

class SampleCompletableUseCase(schedulerProvider: SchedulerProvider) : CompletableUseCase<Unit>(schedulerProvider) {

    override fun execute(parameters: Unit): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}