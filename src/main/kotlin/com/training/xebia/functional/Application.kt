package com.training.xebia.functional

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.resourceScope
import com.sksamuel.hoplite.ConfigLoader
import com.training.xebia.functional.configure.configure
import com.training.xebia.functional.configure.configureHTTP
import com.training.xebia.functional.configure.configureRoutes
import com.training.xebia.functional.env.Dependencies
import com.training.xebia.functional.env.Env
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    SuspendApp {
        resourceScope {
            val envConfig = ConfigLoader().loadConfigOrThrow<Env.Config>("/application.conf")
            val dependencies = Dependencies(envConfig)

            embeddedServer(Netty, port = envConfig.server.port, host = envConfig.server.host) {
                configure()
                configureHTTP()
                configureRoutes(envConfig, dependencies)
            }.start(wait = true)
        }
    }
}
