package com.viinsoft.library

import com.viinsoft.cleanarch.domain.MaybeUseCase
import com.viinsoft.cleanarch.helper.SchedulerProvider
import io.reactivex.Maybe

class SampleMaybeUseCase(schedulerProvider: SchedulerProvider) : MaybeUseCase<Unit, Unit>(schedulerProvider) {

    override fun execute(parameters: Unit): Maybe<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}