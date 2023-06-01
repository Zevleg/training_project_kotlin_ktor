package com.training.xebia.functional

import arrow.fx.coroutines.resourceScope
import com.sksamuel.hoplite.ConfigLoader
import com.training.xebia.functional.configure.configureRoutes
import com.training.xebia.functional.configure.configureSerialization
import com.training.xebia.functional.env.Dependencies
import com.training.xebia.functional.env.Env
import io.kotest.core.spec.style.StringSpec
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import junit.framework.TestCase
import kotlin.test.assertEquals

class ApplicationTest : StringSpec({

    val envConfig = ConfigLoader().loadConfigOrThrow<Env.Config>("/application.conf")

    "Health true Test" {
        resourceScope {
            val dependencies = Dependencies(envConfig)
            testApplication {
                application {
                    configureRoutes(envConfig, dependencies)
                    configureSerialization()
                }
                val response = client.get("/health/true")
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(
                    "{" +
                            "\"service\":{" +
                            "\"name\":\"healthCheckApi\"," +
                            "\"status\":{" +
                            "\"type\":\"com.training.xebia.functional.model.Status.Healthy\"," +
                            "\"code\":200" +
                            "}" +
                            "}" +
                            "}", response.bodyAsText()
                )
            }
        }
    }

    "Health false Test" {
        resourceScope {
            val dependencies = Dependencies(envConfig)
            testApplication {
                application {
                    configureRoutes(envConfig, dependencies)
                    configureSerialization()
                }
                val response = client.get("/health/false")
                assertEquals(HttpStatusCode.ServiceUnavailable, response.status)
                assertEquals(
                    "{" +
                            "\"service\":{" +
                            "\"name\":\"healthCheckApi\"," +
                            "\"status\":{" +
                            "\"type\":\"com.training.xebia.functional.model.Status.Unhealthy\"," +
                            "\"code\":503," +
                            "\"description\":\"The service is unavailable because you are sending false\"" +
                            "}" +
                            "}" +
                            "}", response.bodyAsText()
                )
            }
        }
    }

    "Test Swagger" {
        resourceScope {
            val dependencies = Dependencies(envConfig)
            testApplication {
                application {
                    configureRoutes(envConfig, dependencies)
                }
                client.get("/swagger").apply {
                    TestCase.assertEquals(HttpStatusCode.OK, status)
                }
            }
        }
    }
})
