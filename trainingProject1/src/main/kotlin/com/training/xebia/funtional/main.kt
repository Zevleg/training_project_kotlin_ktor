package com.training.xebia.funtional

import com.sksamuel.hoplite.ConfigLoader
import com.training.xebia.funtional.env.Env
import com.training.xebia.funtional.model.Response
import com.training.xebia.funtional.model.Service
import com.training.xebia.funtional.model.Status
import guru.zoroark.tegral.openapi.dsl.*
import guru.zoroark.tegral.openapi.ktor.TegralOpenApiKtor
import guru.zoroark.tegral.openapi.ktor.describe
import guru.zoroark.tegral.openapi.ktor.openApiEndpoint
import guru.zoroark.tegral.openapi.ktorui.TegralSwaggerUiKtor
import guru.zoroark.tegral.openapi.ktorui.swaggerUiEndpoint
import io.ktor.client.plugins.resources.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.*

@Serializable @Resource("/health/{check}")
class Health(val check: Boolean)

fun main(args: Array<String>) {
    val config = ConfigLoader().loadConfigOrThrow<Env.Config>("/application.conf")

    /*val config = ConfigLoaderBuilder.default()
        .addResourceSource("/application.conf")
        .build()
        .loadConfigOrThrow<Config>()*/

    embeddedServer(Netty, port = config.server.port, host = config.server.host) {
        install(Resources)
        install(TegralOpenApiKtor)
        install(TegralSwaggerUiKtor)
        configure()
    }.start(wait = true)
}

fun Application.configure() {
    routing {
        openApiEndpoint("/healthCheckApi")
        swaggerUiEndpoint(path = "/swagger", openApiPath = "/healthCheckApi")
        get<Health> { health ->
            when(health.check){
                false -> {
                    val status =  Status.Unhealthy(503,"The service is unavailable because you are sending false")
                    val service = Service("healthCheckApi", status)
                    val response = Response(service)
                    call.respondText("Wrong! --> response body ${response}", status = HttpStatusCode.ServiceUnavailable)
                }
                true -> {
                    val status =  Status.Healthy()
                    val service = Service("healthCheckApi", status)
                    val response = Response(service)
                    call.respondText("com.training.xebia.funtional.Health check --> response body ${response}")
                }
            }
        } describe {
            description = "com.training.xebia.funtional.Health check endpoint"
        }
    }
}
