package com.training.xebia.functional.routes

import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.model.*
import com.training.xebia.functional.persistence.user.UsersList
import com.training.xebia.functional.service.UserService
import com.training.xebia.functional.utils.statusCode
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

fun Routing.userSubscriptionRoute(userService: UserService) {
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

        val users = userService.find(UserId(1)).mapLeft { statusCode(HttpStatusCode.BadRequest) }
        users

        users.onRight {

            call.respond(
                status = HttpStatusCode.OK,
                UsersList(it)
            )
        }
        users.onLeft {
            call.respond(
                status = HttpStatusCode.BadRequest,
                it.toString()
            )
        }
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
