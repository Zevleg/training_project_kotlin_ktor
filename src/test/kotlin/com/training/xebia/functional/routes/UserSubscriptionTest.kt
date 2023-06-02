package com.training.xebia.functional.routes

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.UserResponse
import com.training.xebia.functional.domain.Users
import com.training.xebia.functional.persistence.user.UserPersistence
import com.training.xebia.functional.service.UserService
import com.training.xebia.functional.testApp
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

class UserSubscriptionTest : StringSpec({
    suspend fun <A> userSubscription(test: suspend HttpClient.() -> A): A =
        testApp({
            SuspendApp {
                resourceScope {
                    routing { userSubscriptionRoute(userService) }
                }
            }
        }, test)

    "GET /users/subscriptions/id/2 returns status code 200" {
        userSubscription {
            get("/users/subscriptions/id/2").status
        } shouldBe HttpStatusCode.OK
    }

    "GET /users/subscriptions/users returns status code 200" {
        userSubscription {
            get("/users/subscriptions/users?ids=1,2,3,4").status
        } shouldBe HttpStatusCode.OK
    }

    "GET /users/subscriptions/slackUserId/{slackUserId} returns status code 200" {
        userSubscription {
            get("/users/subscriptions/slackUserId/zeifer18").status
        } shouldBe HttpStatusCode.OK
    }

    "GET /users/subscriptions/slackUserId/{slackUserId} returns body" {
        userSubscription {
            get("/users/subscriptions/slackUserId/zeifer18").bodyAsText()
        } shouldBe "{\"user\":{\"id\":1,\"slackUserId\":\"zeifer18\"}}"
    }

    "DELETE /users/subscriptions/id/2 returns body" {
        userSubscription {
            delete("/users/subscriptions/id/2"){
                makeBody(UserResponse.OperationOK(2, "User deleted"))
            }.bodyAsText()
        } shouldBe "{\"userId\":2,\"message\":\"User deleted\"}"
    }
})


@Serializable
data class Subscription(val owner: String, val name: String)

private val persistence = object : UserPersistence {
    override suspend fun findUsers(userIds: List<UserId>): List<Users> {
        val users1 = Users(UserId(1), SlackUserId("zeifer18"))
        val users2 = Users(UserId(2), SlackUserId("LauraC"))
        val users3 = Users(UserId(3), SlackUserId("KhateG"))

        return listOf(users1, users2, users3)
    }

    override suspend fun find(userId: UserId): Users? =
        Users(UserId(2), SlackUserId("zeifer18"))

    override suspend fun findSlackUser(slackUserId: SlackUserId): Users? =
        Users(UserId(1), SlackUserId("zeifer18"))

    override suspend fun getOrInsertSlackUser(slackUserId: SlackUserId): Users {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSlackUser(slackUserId: SlackUserId) {}

    override suspend fun deleteUser(userId: UserId) {}
}

private val userService = UserService(persistence)

private fun HttpRequestBuilder.makeBody(userResponse: UserResponse) {
    contentType(ContentType.Application.Json)
    setBody(userResponse)
}

@Serializable
data class Repository(val owner: String, val name: String)
