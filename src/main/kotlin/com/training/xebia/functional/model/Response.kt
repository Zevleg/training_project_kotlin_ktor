package com.training.xebia.functional.model

import arrow.optics.optics
import kotlinx.serialization.Serializable

@optics
@Serializable
data class Response(val service: Service) {
    companion object
}

@optics
@Serializable
data class Service(val name: String, val status: Status) {
    companion object
}

@optics
@Serializable
sealed class Status {
    abstract val code: Int

    @optics
    @Serializable
    data class Healthy(override val code: Int = 200) : Status() {
        companion object
    }

    @optics
    @Serializable
    data class Unhealthy(override val code: Int = 503, val description: String?) : Status() {
        companion object
    }
}
