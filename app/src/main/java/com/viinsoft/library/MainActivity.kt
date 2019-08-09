package com.viinsoft.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.viinsoft.cleanarch.model.UseCaseState

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SampleCompletableUseCase(AppSchedulerProvider()).invoke(Unit) {
            when (it) {
                is UseCaseState.LoadContent -> {
                }
                is UseCaseState.Complete -> {
                }
                is UseCaseState.Error -> {
                }
            }
        }

        SampleMaybeUseCase(AppSchedulerProvider()).invoke(Unit) {
            when (it) {
                is UseCaseState.LoadContent -> {
                }
                is UseCaseState.Complete -> {
                }
                is UseCaseState.Error -> {
                }
            }
        }
    }
}
