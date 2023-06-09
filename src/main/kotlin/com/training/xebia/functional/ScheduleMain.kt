package com.training.xebia.functional

import arrow.core.Either
import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.delay

@ExperimentalTime
suspend fun main(): Unit {
    suspend fun apiCall(): Unit {
        println("apiCall . . .")
        throw RuntimeException("Overloaded service")
    }

    //sampleStart
    val circuitBreaker = CircuitBreaker.of(
        maxFailures = 2,
        resetTimeout = 2.seconds,
        exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
        maxResetTimeout = 60.seconds, // limit exponential back-off time
    )

    suspend fun <A> resilient(schedule: Schedule<Throwable, *>, f: suspend () -> A): A =
        schedule.retry { circuitBreaker.protectOrThrow(f) }

    Either.catch {
        resilient(Schedule.recurs(5), ::apiCall)
    }.let { println("recurs(5) apiCall twice and 4x short-circuit result from CircuitBreaker: $it") }

    delay(2000)
    println("CircuitBreaker ready to half-open")

    // Retry once and when the CircuitBreaker opens after 2 failures then retry with exponential back-off with same time as CircuitBreaker's resetTimeout
    val fiveTimesWithBackOff = Schedule.recurs<Throwable>(1) andThen
            Schedule.exponential(2.seconds) and Schedule.recurs(5)

    Either.catch {
        resilient(fiveTimesWithBackOff, ::apiCall)
    }.let { println("exponential(2.seconds) and recurs(5) always retries with actual apiCall: $it") }
    //sampleEnd
}

/*@ExperimentalTime
suspend fun main(): Unit {
//sampleStart
    val circuitBreaker = CircuitBreaker.of(
        maxFailures = 2,
        resetTimeout = 2.seconds,
        exponentialBackoffFactor = 1.2,
        maxResetTimeout = 60.seconds,
    )
    circuitBreaker.protectOrThrow { "I am in Closed: ${circuitBreaker.state()}" }.also(::println)

    println("Service getting overloaded . . .")

    Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)
    Either.catch { circuitBreaker.protectOrThrow { throw RuntimeException("Service overloaded") } }.also(::println)
    circuitBreaker.protectEither { }.also { println("I am Open and short-circuit with ${it}. ${circuitBreaker.state()}") }

    println("Service recovering . . .").also { delay(2000) }

    circuitBreaker.protectOrThrow { "I am running test-request in HalfOpen: ${circuitBreaker.state()}" }.also(::println)
    println("I am back to normal state closed ${circuitBreaker.state()}")
//sampleEnd
}*/
