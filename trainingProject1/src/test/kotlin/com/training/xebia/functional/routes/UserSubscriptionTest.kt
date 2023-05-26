package com.training.xebia.functional.routes

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/*class UserSubscriptionTest : StringSpec({
    suspend fun <A> subscriptions(test: suspend HttpClient.() -> A): A =
        testApp({
            routing { subscriptionRoute(SubscriptionService()) }
        }, test)

    "GET /subscription/42 returns status code 200" {
        subscriptions {
            get("/subscription/42").status
        } shouldBe HttpStatusCode.OK
    }

    "GET /subscription/42 returns id object" {
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
    }
})*/



private fun HttpRequestBuilder.testRepo() {
    contentType(ContentType.Application.Json)
    setBody(Repository("foo", "bar"))
}

@Serializable
data class Repository(val owner: String, val name: String)
