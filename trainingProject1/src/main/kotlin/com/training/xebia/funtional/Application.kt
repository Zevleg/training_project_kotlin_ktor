package com.training.xebia.funtional

import com.sksamuel.hoplite.ConfigLoader
import com.training.xebia.funtional.configure.configureHTTP
import com.training.xebia.funtional.configure.configureRoutes
import com.training.xebia.funtional.configure.configureSerialization
import com.training.xebia.funtional.env.Env
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val config = ConfigLoader().loadConfigOrThrow<Env.Config>("/application.conf")

    embeddedServer(Netty, port = config.server.port, host = config.server.host) {
        configureHTTP()
        configureSerialization()
        configureRoutes(config)
    }.start(wait = true)
}
