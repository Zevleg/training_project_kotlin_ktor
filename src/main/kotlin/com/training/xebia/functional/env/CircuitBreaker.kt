package com.training.xebia.functional.env

import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.ResourceScope
import kotlin.time.Duration.Companion.seconds

suspend fun ResourceScope.setCircuitBreaker(): CircuitBreaker {
    return CircuitBreaker.of(
        maxFailures = 2,
        resetTimeout = 2.seconds,
        exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
        maxResetTimeout = 60.seconds, // limit exponential back-off time
    )
}
