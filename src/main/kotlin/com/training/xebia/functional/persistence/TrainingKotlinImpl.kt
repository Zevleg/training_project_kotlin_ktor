package com.training.xebia.functional.persistence

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.training.xebia.functional.domain.Users
import com.training.xebia.functional.persistence.repositories.Repositories
import com.training.xebia.functional.persistence.repositories.RepositoriesQueries
import com.training.xebia.functional.persistence.subscriptions.Subscriptions
import com.training.xebia.functional.persistence.subscriptions.SubscriptionsQueries
import com.training.xebia.functional.persistence.user.UsersQueries
import kotlin.reflect.KClass

internal val KClass<TrainingKotlin>.schema: SqlSchema
  get() = TrainingKotlinImpl.Schema

internal fun KClass<TrainingKotlin>.newInstance(
    driver: SqlDriver,
    repositoriesAdapter: Repositories.Adapter,
    subscriptionsAdapter: Subscriptions.Adapter,
    usersAdapter: Users.Adapter,
): TrainingKotlin = TrainingKotlinImpl(driver, repositoriesAdapter, subscriptionsAdapter,
    usersAdapter)

private class TrainingKotlinImpl(
    driver: SqlDriver,
    repositoriesAdapter: Repositories.Adapter,
    subscriptionsAdapter: Subscriptions.Adapter,
    usersAdapter: Users.Adapter,
) : TransacterImpl(driver), TrainingKotlin {
  public override val repositoriesQueries: RepositoriesQueries = RepositoriesQueries(driver,
      repositoriesAdapter)

  public override val subscriptionsQueries: SubscriptionsQueries = SubscriptionsQueries(driver,
      subscriptionsAdapter, repositoriesAdapter)

  public override val usersQueries: UsersQueries = UsersQueries(driver, usersAdapter)

  public object Schema : SqlSchema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): QueryResult<Unit> {
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS repositories (
          |  id BIGSERIAL PRIMARY KEY,
          |  owner VARCHAR NOT NULL,
          |  repository VARCHAR NOT NULL,
          |  CONSTRAINT unique_repos UNIQUE (owner, repository)
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS subscriptions (
          |  user_id BIGSERIAL NOT NULL,
          |  repository_id BIGSERIAL NOT NULL,
          |  subscribed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
          |  PRIMARY KEY (user_id, repository_id),
          |  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
          |  FOREIGN KEY (repository_id) REFERENCES repositories(id) ON DELETE CASCADE
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS users(
          |  id BIGSERIAL PRIMARY KEY,
          |  slack_user_id VARCHAR UNIQUE NOT NULL
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int,
      vararg callbacks: AfterVersion,
    ): QueryResult<Unit> = QueryResult.Unit
  }
}
