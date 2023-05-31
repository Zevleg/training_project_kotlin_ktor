package com.training.xebia.functional.env

import arrow.fx.coroutines.ResourceScope
import com.training.xebia.functional.persistence.user.UserPersistence
import com.training.xebia.functional.service.UserService

class Dependencies(
    val userService: UserService
)

suspend fun ResourceScope.Dependencies(env: Env.Config): Dependencies {
    val dataSource = hikari()
    val dataSourceWithEnv = hikariWithEnv(env.postgresContainer)
    val trainingKotlin = trainingKotlinDataSource(dataSource)
    val trainingKotlinWithEnv = trainingKotlinDataSource(dataSourceWithEnv)
    val userPersistence = UserPersistence(trainingKotlin.usersQueries)

    return Dependencies(
        UserService(userPersistence)
    )
}