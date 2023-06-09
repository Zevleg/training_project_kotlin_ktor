package com.training.xebia.functional.service

import arrow.fx.coroutines.Schedule
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.UserNotFound
import com.training.xebia.functional.env.hikariWithEnv
import com.training.xebia.functional.env.setCircuitBreaker
import com.training.xebia.functional.env.trainingKotlinDataSource
import com.training.xebia.functional.install
import com.training.xebia.functional.persistence.PostgreSQLContainer
import com.training.xebia.functional.persistence.user.UserPersistence
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.core.spec.style.StringSpec
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class SubscriptionServiceSpec : StringSpec({
    val postgres = install(PostgreSQLContainer.resource())

    val users = install {
        val trainingKotlin = trainingKotlinDataSource(hikariWithEnv(postgres.get().config()))
        val circuitBreaker = setCircuitBreaker()

        @OptIn(ExperimentalTime::class)
        val defaultRetrySchedule: Schedule<Throwable, Unit> =
            Schedule.recurs<Throwable>(3)
                .and(Schedule.exponential(1.seconds))
                .void()

        UserPersistence(circuitBreaker, trainingKotlin.usersQueries, defaultRetrySchedule)
    }

    afterTest { postgres.get().clear() }

    val userId = UserId(1)

    "User not found by id" {
        val service = UserService(users.get())

        service.find(userId).shouldBeLeft(UserNotFound(userId, null))
    }
})
