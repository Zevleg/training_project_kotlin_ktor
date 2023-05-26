package com.training.xebia.functional.utils

import io.ktor.http.*
import io.ktor.http.content.*

/** Small utility to turn HttpStatusCode into OutgoingContent. */
fun statusCode(statusCode: HttpStatusCode) = object : OutgoingContent.NoContent() {
    override val status: HttpStatusCode = statusCode
}
