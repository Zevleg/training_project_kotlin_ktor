package com.training.xebia.functional.domain

import app.cash.sqldelight.ColumnAdapter
import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@JvmInline
@Serializable
value class UserId(val id: Long){
    companion object {
        val adapter: ColumnAdapter<UserId, Long> = object : ColumnAdapter<UserId, Long> {
            override fun decode(databaseValue: Long): UserId = UserId(databaseValue)
            override fun encode(value: UserId): Long = value.id
        }
    }
}

@JvmInline
@Serializable
value class SlackUserId(val slackUserId: String){
    companion object {
        val adapter: ColumnAdapter<SlackUserId, String> = object : ColumnAdapter<SlackUserId, String> {
            override fun decode(databaseValue: String): SlackUserId = SlackUserId(databaseValue)
            override fun encode(value: SlackUserId): String = value.slackUserId
        }
    }
}

@optics
@Serializable
data class User(val id: UserId, val slackUserId: SlackUserId) {
    companion object
}

@optics
@Serializable
data class Users(val users: List<User>) {
    companion object
}

@Serializable
sealed interface UserError {
    fun toJson(): String = Json.encodeToString(serializer(), this)
}

@Serializable
data class UserNotFound(val userId: UserId, val slackUserId: SlackUserId? = null) : UserError
