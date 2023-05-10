package com.training.xebia.funtional.routes

import com.training.xebia.funtional.model.Users
import com.training.xebia.funtional.model.UsersResponse
import com.training.xebia.funtional.repository.usersRepository
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.put
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import mu.KLogger
import mu.KotlinLogging

@Serializable
@Resource("/users/subscriptions")
class UserSubscription

fun Routing.userSubscriptionRoute() {
    val logger: KLogger = KotlinLogging.logger { }

    put<UserSubscription> {
        logger.info { "Got PUT Subscription request" }

        val users = call.receive<Users>()
        call.respond(
            status = HttpStatusCode.OK,
            UsersResponse(users.users)
        )
    } describe {
        description = "Update a Subscription"
    }

    get<UserSubscription> {
        logger.info { "Got GET Subscription request" }

        call.respond(
            status = HttpStatusCode.OK,
            UsersResponse(usersRepository.users)
        )
    } describe {
        description = "Retrieve a Subscription"
    }

    delete<UserSubscription> {
        logger.info { "Got DELETE Subscription request" }

        val users = call.receive<Users>()

        call.respond(
            status = HttpStatusCode.OK,
            UsersResponse(users.users)
        )
    } describe {
        description = "Delete a Subscription"
    }
}
