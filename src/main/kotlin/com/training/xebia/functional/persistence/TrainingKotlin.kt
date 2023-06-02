package com.training.xebia.functional.persistence

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.training.xebia.functional.domain.Users
import com.training.xebia.functional.persistence.repositories.Repositories
import com.training.xebia.functional.persistence.repositories.RepositoriesQueries
import com.training.xebia.functional.persistence.subscriptions.Subscriptions
import com.training.xebia.functional.persistence.subscriptions.SubscriptionsQueries
import com.training.xebia.functional.persistence.user.UsersQueries

public interface TrainingKotlin : Transacter {
  public val repositoriesQueries: RepositoriesQueries

  public val subscriptionsQueries: SubscriptionsQueries

  public val usersQueries: UsersQueries

  public companion object {
    public val Schema: SqlSchema
      get() = TrainingKotlin::class.schema

    public operator fun invoke(
        driver: SqlDriver,
        repositoriesAdapter: Repositories.Adapter,
        subscriptionsAdapter: Subscriptions.Adapter,
        usersAdapter: Users.Adapter,
    ): TrainingKotlin = TrainingKotlin::class.newInstance(driver, repositoriesAdapter,
        subscriptionsAdapter, usersAdapter)
  }
}
