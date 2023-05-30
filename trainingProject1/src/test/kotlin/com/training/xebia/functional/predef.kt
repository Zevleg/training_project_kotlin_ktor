package com.training.xebia.functional

import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.continuations.resource
import io.kotest.assertions.arrow.fx.coroutines.extension
import io.kotest.core.extensions.LazyMaterialized
import io.kotest.core.spec.Spec

fun <MATERIALIZED> Spec.install(
    acquire: suspend ResourceScope.() -> MATERIALIZED,
): LazyMaterialized<MATERIALIZED> {
    val ext = resource(acquire).extension()
    extensions(ext)
    return ext.mount { }
}
