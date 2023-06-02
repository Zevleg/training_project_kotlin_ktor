package com.training.xebia.functional.configure

import com.training.xebia.functional.env.Dependencies
import com.training.xebia.functional.env.Env
import com.training.xebia.functional.routes.healthRoute
import com.training.xebia.functional.routes.userSubscriptionRoute
import com.training.xebia.functional.routes.validateRepoRoute
import guru.zoroark.tegral.openapi.ktor.openApiEndpoint
import guru.zoroark.tegral.openapi.ktorui.swaggerUiEndpoint
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes(config: Env.Config, dependencies: Dependencies) {

    routing {
        openApiEndpoint("/openapi")
        swaggerUiEndpoint(path = "/swagger", openApiPath = "/openapi")

        healthRoute()
        validateRepoRoute(config.github)
        userSubscriptionRoute(dependencies.userService)
    }
}
