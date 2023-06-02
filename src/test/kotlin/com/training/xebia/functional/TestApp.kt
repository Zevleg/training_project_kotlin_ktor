package com.training.xebia.functional

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.continuations.resource
import io.kotest.assertions.arrow.fx.coroutines.extension
import io.kotest.core.extensions.LazyMaterialized
import io.kotest.core.spec.Spec
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.CompletableDeferred

suspend fun <A> testApp(
    setup: Application.() -> Unit,
    test: suspend HttpClient.() -> A,
): A {
    val result = CompletableDeferred<A>()
    testApplication {
        application {
            module()
            setup()
        }
        createClient {
            install(ContentNegotiation) { json() }
            expectSuccess = false
        }.use { result.complete(test(it)) }
    }
    return result.await()
}

fun <MATERIALIZED> Spec.install(resource: Resource<MATERIALIZED>): LazyMaterialized<MATERIALIZED> {
    val ext = resource.extension()
    extensions(ext)
    return ext.mount { }
}

fun <MATERIALIZED> Spec.install(
    acquire: suspend ResourceScope.() -> MATERIALIZED,
): LazyMaterialized<MATERIALIZED> {
    val ext = resource(acquire).extension()
    extensions(ext)
    return ext.mount { }
}
