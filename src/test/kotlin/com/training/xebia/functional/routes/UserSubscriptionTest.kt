package com.training.xebia.functional.routes

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.sksamuel.hoplite.ConfigLoader
import com.training.xebia.functional.env.Dependencies
import com.training.xebia.functional.env.Env
import com.training.xebia.functional.module
import com.training.xebia.functional.testApp
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable

class UserSubscriptionTest : StringSpec({
    suspend fun <A> userSubscription(test: suspend HttpClient.() -> A): A =
        testApp({
            SuspendApp {
                resourceScope {
                    val envConfig = ConfigLoader().loadConfigOrThrow<Env.Config>("/application.conf")
                    val dependencies = Dependencies(envConfig)
                    routing { userSubscriptionRoute(dependencies.userService) }
                }
            }
        }, test)

    "GET /subscription returns status code 200" {
        userSubscription {
            get("/id/2").status
        } shouldBe HttpStatusCode.OK
    }

    "GET /subscription returns status code 20" {
        testApplication {
            application {
                module()
            }

            // when
            client.get("/users/subscriptions/id/1") {
                contentType(ContentType.Application.Json) // request header
                //setBody(Json.encodeToString(customer)) // request body
            }.apply {
                // then
                shouldBe(HttpStatusCode.OK)
            }
        }
    }

    /*"GET /subscription/42 returns id object" {
        subscriptions {
            get("/subscription/42").bodyAsText()
        } shouldBe "{\n" +
                "  \"slackUserId\" : \"42\"\n" +
                "}"
    }

    "POST /subscription/42 returns status code 201" {
        subscriptions {
            post("/subscription/42") { testRepo() }.status
        } shouldBe HttpStatusCode.Created
    }

    "DELETE /subscription/42 returns status code 204" {
        subscriptions {
            delete("/subscription/42") { testRepo() }.status
        } shouldBe HttpStatusCode.NoContent
    }*/
})



private fun HttpRequestBuilder.testRepo() {
    contentType(ContentType.Application.Json)
    setBody(Repository("foo", "bar"))
}

@Serializable
data class Repository(val owner: String, val name: String)
