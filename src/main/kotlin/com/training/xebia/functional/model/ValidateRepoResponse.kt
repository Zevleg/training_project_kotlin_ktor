package com.training.xebia.functional.model

import arrow.optics.optics
import kotlinx.serialization.Serializable

@optics
@Serializable
data class ValidateRepoResponse(val status: Status, val repoStatusResponse: RepoStatusResponse) {
    companion object
}

@optics
@Serializable
sealed class RepoStatusResponse {
    @optics
    @Serializable
    class Reachable(val owner: String, val repo: String, val doesExist: Boolean): RepoStatusResponse() {
        companion object
    }

    @optics
    @Serializable
    class NotAvailable(val owner: String, val repo: String, val doesExist: Boolean, val errorMsg: String): RepoStatusResponse()
}