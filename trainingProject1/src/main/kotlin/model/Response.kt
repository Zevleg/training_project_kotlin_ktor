package model

import arrow.optics.optics

@optics
data class Response(val service: Service) {
    companion object
}

@optics
data class Service(val name: String, val status: Status) {
    companion object
}

@optics
sealed class Status {
    abstract val code: Int

    @optics
    data class Healthy(override val code: Int = 200) : Status() {
        companion object
    }

    @optics
    data class Unhealthy(override val code: Int = 503, val description: String) : Status() {
        companion object
    }
}
