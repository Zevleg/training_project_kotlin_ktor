package com.training.xebia.functional.persistence

import com.training.xebia.functional.domain.Owner
import com.training.xebia.functional.domain.Repo
import java.time.LocalDateTime

public data class FindAll(
  public val owner: Owner,
  public val repository: Repo,
  public val subscribed_at: LocalDateTime,
)
