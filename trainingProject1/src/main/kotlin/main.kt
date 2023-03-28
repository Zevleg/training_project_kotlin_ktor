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
import kotlinx.serialization.Serializable

@Serializable @Resource("/health/{check}")
class Health(val check: Boolean)

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
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
                false -> call.respondText("Wrong!", status = HttpStatusCode.ServiceUnavailable)
                true -> call.respondText("Health check")
            }
        } describe {
            description = "Health check endpoint"
        }
    }
}
