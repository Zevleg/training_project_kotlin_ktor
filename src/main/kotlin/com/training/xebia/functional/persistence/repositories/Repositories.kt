package com.training.xebia.functional.persistence.repositories

import app.cash.sqldelight.ColumnAdapter
import com.training.xebia.functional.domain.Owner
import com.training.xebia.functional.domain.Repo
import com.training.xebia.functional.domain.RepositoryId

public data class Repositories(
  public val id: RepositoryId,
  public val owner: Owner,
  public val repository: Repo,
) {
  public class Adapter(
    public val idAdapter: ColumnAdapter<RepositoryId, Long>,
    public val ownerAdapter: ColumnAdapter<Owner, String>,
    public val repositoryAdapter: ColumnAdapter<Repo, String>,
  )
}
