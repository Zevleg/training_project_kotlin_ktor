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

@Serializable
public data class Users(
  public val id: UserId,
  public val slackUserId: SlackUserId,
) {
  public class Adapter(
    public val idAdapter: ColumnAdapter<UserId, Long>,
    public val slackUserIdAdapter: ColumnAdapter<SlackUserId, String>,
  )
}

@optics
@Serializable
data class UsersList(val users: List<Users>) {
  companion object
}

@Serializable
sealed interface UserError {
  fun toJson(): String = Json.encodeToString(serializer(), this)
}

@Serializable
data class UserNotFound(val userId: UserId? = null, val slackUserId: SlackUserId? = null) : UserError

@Serializable
data class UsersNotFound(val userId: List<UserId>) : UserError

@Serializable
data class UserGetOrCreateError(val slackUserId: SlackUserId) : UserError

@Serializable
sealed class UserResponse() {
  @Serializable
  class OperationOK(val userId: Long, val message: String): UserResponse()

  @Serializable
  class User(val user: Users): UserResponse()

  @Serializable
  class UsersList(val users: List<Users>): UserResponse()

  @Serializable
  class ErrorUserIdResponse(val userId: Long, val errorMsg: String): UserResponse()

  @Serializable
  class ErrorUserIdsResponse(val userId: List<UserId>, val errorMsg: String): UserResponse()

  @Serializable
  class ErrorSlackUserIdResponse(val slackUserId: String, val errorMsg: String): UserResponse()
}
