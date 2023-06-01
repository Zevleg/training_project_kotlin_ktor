package com.training.xebia.functional.routes

import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.UserResponse
import com.training.xebia.functional.model.*
import com.training.xebia.functional.service.UserService
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
class UserSubscription{
    @Serializable
    @Resource("/users")
    class RequestUsersID(val parent: UserSubscription, val ids: String)

    @Serializable
    @Resource("/id/{id}")
    class RequestId(val parent: UserSubscription, val id: Long)

    @Serializable
    @Resource("/slackUserId/{slackUserId}")
    class RequestSlackUserId(val parent: UserSubscription, val slackUserId: String)
}

fun Routing.userSubscriptionRoute(userService: UserService) {
    val logger: KLogger = KotlinLogging.logger { }

    put<UserSubscription.RequestSlackUserId> { req ->
        logger.info { "Got PUT Subscription request" }

        val users = userService.getOrCreate(SlackUserId(req.slackUserId))

        users.onRight {

            call.respond(
                status = HttpStatusCode.OK,
                UserResponse.User(it)
            )
        }
        users.onLeft {
            call.respond(
                status = HttpStatusCode.ServiceUnavailable,
                UserResponse.ErrorSlackUserIdResponse(req.slackUserId, USER_NOT_CREATED)
            )
        }
    } describe {
        description = "Update a Subscription"
    }

    get<UserSubscription.RequestUsersID> { req ->
        logger.info { "Got GET Subscription request by slack user id" }

        val idList = req.ids.split(",").map { UserId(it.toLong()) }

        val users = userService.findUsers(idList)

        users.onRight {

            call.respond(
                status = HttpStatusCode.OK,
                UserResponse.UsersList(it)
            )
        }
        users.onLeft {
            call.respond(
                status = HttpStatusCode.NotFound,
                UserResponse.ErrorUserIdsResponse(idList, USER_NOT_FOUND)
            )
        }
    } describe {
        description = "Retrieve a Subscription"
    }

    get<UserSubscription.RequestId> { req ->
        logger.info { "Got GET Subscription request by id" }

        val users = userService.find(UserId(req.id))

        users.onRight {

            call.respond(
                status = HttpStatusCode.OK,
                UserResponse.User(it)
            )
        }
        users.onLeft {
            call.respond(
                status = HttpStatusCode.NotFound,
                UserResponse.ErrorUserIdResponse(req.id, USER_NOT_FOUND)
            )
        }
    } describe {
        description = "Retrieve a Subscription"
    }

    get<UserSubscription.RequestSlackUserId> { req ->
        logger.info { "Got GET Subscription request by slack user id" }

        val users = userService.findSlackUser(SlackUserId(req.slackUserId))

        users.onRight {

            call.respond(
                status = HttpStatusCode.OK,
                UserResponse.User(it)
            )
        }
        users.onLeft {
            call.respond(
                status = HttpStatusCode.NotFound,
                UserResponse.ErrorSlackUserIdResponse(req.slackUserId, USER_NOT_FOUND)
            )
        }
    } describe {
        description = "Retrieve a Subscription"
    }

    delete<UserSubscription.RequestId> { req ->
        logger.info { "Got DELETE Subscription request" }

        userService.deleteUser(UserId(req.id))

        call.respond(
            status = HttpStatusCode.OK,
            UserResponse.OperationOK(req.id, USER_DELETED)
        )
    } describe {
        description = "Delete a Subscription"
    }
}

private const val USER_NOT_FOUND =
    "User not found"

private const val USER_NOT_CREATED =
    "Could not create user"

private const val USER_DELETED =
    "User deleted"
