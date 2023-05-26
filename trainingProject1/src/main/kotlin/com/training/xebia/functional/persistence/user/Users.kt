package com.training.xebia.functional.persistence.user

import app.cash.sqldelight.ColumnAdapter
import arrow.optics.optics
import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.domain.UserId
import kotlinx.serialization.Serializable

@Serializable
public data class Users(
  public val id: UserId,
  public val slack_user_id: SlackUserId,
) {
  public class Adapter(
    public val idAdapter: ColumnAdapter<UserId, Long>,
    public val slack_user_idAdapter: ColumnAdapter<SlackUserId, String>,
  )
}

@optics
@Serializable
data class UsersList(val users: List<Users>) {
  companion object
}
