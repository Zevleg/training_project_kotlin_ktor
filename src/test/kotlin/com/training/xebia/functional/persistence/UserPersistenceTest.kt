package com.training.xebia.functional.persistence

import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.env.hikariWithEnv
import com.training.xebia.functional.env.trainingKotlinDataSource
import com.training.xebia.functional.install
import com.training.xebia.functional.persistence.user.UserPersistence
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SubscriptionPersistenceTest : StringSpec({

    val postgres = install(PostgreSQLContainer.resource())
    val users = install {
        val trainingKotlin = trainingKotlinDataSource(hikariWithEnv(postgres.get().config()))
        UserPersistence(trainingKotlin.usersQueries)
    }

    afterTest { postgres.get().clear() }

    val slackUserId = SlackUserId("myTestUser")

    "Add test user" {
        users.get().getOrInsertSlackUser(slackUserId).slackUserId shouldBe slackUserId
    }

})
