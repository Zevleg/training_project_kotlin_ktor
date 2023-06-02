package com.training.xebia.functional.persistence

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.ResourceDSL
import arrow.fx.coroutines.continuations.ResourceScope
import com.training.xebia.functional.env.Env
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.lifecycle.Startable

class PostgreSQLContainer private constructor() :
    org.testcontainers.containers.PostgreSQLContainer<PostgreSQLContainer>("postgres:14.1-alpine") {

    fun config() = Env.PostgresContainer(jdbcUrl, username, password)

    suspend fun clear() = withContext(Dispatchers.IO) {
        createConnection("").use { conn ->
            conn.prepareStatement("TRUNCATE users CASCADE").use {
                it.executeLargeUpdate()
            }
        }
    }

    companion object {
        fun resource(): Resource<PostgreSQLContainer> = arrow.fx.coroutines.continuations.resource {
            startable { PostgreSQLContainer().waitingFor(Wait.forListeningPort()) }
        }
    }
}

@ResourceDSL
suspend fun <A : Startable> ResourceScope.startable(startable: suspend () -> A): A =
    install({
        startable().also { runInterruptible(Dispatchers.IO, block = it::start) }
    }) { closeable, _ -> withContext(Dispatchers.IO) { closeable.close() } }
