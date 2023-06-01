package com.training.xebia.functional.routes

import com.training.xebia.functional.model.Response
import com.training.xebia.functional.model.Service
import com.training.xebia.functional.model.Status
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/health/{check}")
class Health(val check: Boolean)

fun Routing.healthRoute() {
    get<Health> { health ->
        when(health.check){
            false -> {
                val status =  Status.Unhealthy(503,"The service is unavailable because you are sending false")
                val service = Service("healthCheckApi", status)
                val response = Response(service)
                call.respond(status = HttpStatusCode.ServiceUnavailable, response)
            }
            true -> {
                val status =  Status.Healthy()
                val service = Service("healthCheckApi", status)
                val response = Response(service)
                call.respond(response)
            }
        }
    } describe {
        description = "Health check endpoint"
    }
}
