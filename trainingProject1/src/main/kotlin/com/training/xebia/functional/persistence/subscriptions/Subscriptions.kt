package com.training.xebia.functional.persistence.subscriptions

import app.cash.sqldelight.ColumnAdapter
import com.training.xebia.functional.domain.RepositoryId
import com.training.xebia.functional.domain.UserId
import java.time.LocalDateTime

public data class Subscriptions(
  public val user_id: UserId,
  public val repository_id: RepositoryId,
  public val subscribed_at: LocalDateTime,
) {
  public class Adapter(
    public val user_idAdapter: ColumnAdapter<UserId, Long>,
    public val repository_idAdapter: ColumnAdapter<RepositoryId, Long>,
  )
}
