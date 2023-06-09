package com.training.xebia.functional.persistence

import arrow.fx.coroutines.Schedule
import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.env.hikariWithEnv
import com.training.xebia.functional.env.setCircuitBreaker
import com.training.xebia.functional.env.trainingKotlinDataSource
import com.training.xebia.functional.install
import com.training.xebia.functional.persistence.user.UserPersistence
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class SubscriptionPersistenceTest : StringSpec({

    @OptIn(ExperimentalTime::class)
    val defaultRetrySchedule: Schedule<Throwable, Unit> =
        Schedule.recurs<Throwable>(3)
            .and(Schedule.exponential(1.seconds))
            .void()

    val postgres = install(PostgreSQLContainer.resource())
    val users = install {
        val trainingKotlin = trainingKotlinDataSource(hikariWithEnv(postgres.get().config()))
        val circuitBreaker = setCircuitBreaker()
        UserPersistence(circuitBreaker,trainingKotlin.usersQueries, defaultRetrySchedule)
    }

    afterTest { postgres.get().clear() }

    val slackUserId = SlackUserId("myTestUser")

    "Add test user" {
        users.get().getOrInsertSlackUser(slackUserId).slackUserId shouldBe slackUserId
    }

})
