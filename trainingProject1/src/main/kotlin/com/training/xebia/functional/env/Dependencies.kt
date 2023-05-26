package com.training.xebia.functional.env

import arrow.fx.coroutines.ResourceScope
import com.training.xebia.functional.service.UserService

class Dependencies(
    val userService: UserService
    //val repositories: RepositoriesService,
    //val subscriptions: SubscriptionsService,
)

suspend fun ResourceScope.Dependencies(env: Env.Config): Dependencies {
    val dataSource = hikari()
    val dataSourceWithEnv = hikariWithEnv(env.postgres)
    val trainingKotlin = TrainingKotlinDataSource(dataSource)
    val trainingKotlinWithEnv = TrainingKotlinDataSource(dataSourceWithEnv)

    return Dependencies(
        UserService(trainingKotlin.usersQueries)
        //SubscriptionService(subscriptionsPersistence, users, producer, client),
    )
}