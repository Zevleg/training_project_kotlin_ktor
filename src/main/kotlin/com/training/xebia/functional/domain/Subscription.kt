package com.training.xebia.functional.domain

import arrow.optics.optics
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable


@optics
@Serializable
data class Subscription(val userId: UserId, val repositoryId: RepositoryId, val subscribedAt: LocalDateTime) {
    companion object
}

@optics
@Serializable
data class Subscriptions(val subscriptions: List<Subscription>) {
    companion object
}