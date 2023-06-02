package com.training.xebia.functional.configure

import guru.zoroark.tegral.openapi.ktor.TegralOpenApiKtor
import guru.zoroark.tegral.openapi.ktorui.TegralSwaggerUiKtor
import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.resources.*

fun Application.configure(){
    install(DefaultHeaders)
    configureSerialization()
    install(Resources)
    install(TegralOpenApiKtor) {
        title = "GitHub Subscription App"
        version = "v1"
    }
    install(TegralSwaggerUiKtor)
}
