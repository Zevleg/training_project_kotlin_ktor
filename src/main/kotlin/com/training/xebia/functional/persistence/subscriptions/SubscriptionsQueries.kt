package com.training.xebia.functional.persistence.subscriptions

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcCursor
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import com.training.xebia.functional.domain.Owner
import com.training.xebia.functional.domain.Repo
import com.training.xebia.functional.domain.RepositoryId
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.persistence.FindAll
import com.training.xebia.functional.persistence.repositories.Repositories
import java.time.LocalDateTime

public class SubscriptionsQueries(
    driver: SqlDriver,
    private val subscriptionsAdapter: Subscriptions.Adapter,
    private val repositoriesAdapter: Repositories.Adapter,
) : TransacterImpl(driver) {
  public fun <T : Any> findAll(userId: UserId, mapper: (
    owner: Owner,
    repository: Repo,
    subscribed_at: LocalDateTime,
  ) -> T): Query<T> = FindAllQuery(userId) { cursor ->
    check(cursor is JdbcCursor)
    mapper(
      repositoriesAdapter.ownerAdapter.decode(cursor.getString(0)!!),
      repositoriesAdapter.repositoryAdapter.decode(cursor.getString(1)!!),
      cursor.getObject<LocalDateTime>(2)!!
    )
  }

  public fun findAll(userId: UserId): Query<FindAll> = findAll(userId) { owner, repository,
                                                                         subscribed_at ->
    FindAll(
      owner,
      repository,
      subscribed_at
    )
  }

  public fun findSubscribers(repositoryOwner: Owner, repositoryName: Repo): Query<UserId> =
      FindSubscribersQuery(repositoryOwner, repositoryName) { cursor ->
    check(cursor is JdbcCursor)
    subscriptionsAdapter.user_idAdapter.decode(cursor.getLong(0)!!)
  }

  public fun delete(
    userId: UserId,
    owner: Owner,
    repository: Repo,
  ): Unit {
    driver.execute(2100366016, """
        |DELETE FROM subscriptions
        |WHERE user_id = ? AND repository_id IN (
        |  SELECT repository_id FROM repositories
        |  WHERE owner = ? AND repository = ?
        |)
        """.trimMargin(), 3) {
          check(this is JdbcPreparedStatement)
          bindLong(0, subscriptionsAdapter.user_idAdapter.encode(userId))
          bindString(1, repositoriesAdapter.ownerAdapter.encode(owner))
          bindString(2, repositoriesAdapter.repositoryAdapter.encode(repository))
        }
    notifyQueries(2100366016) { emit ->
      emit("subscriptions")
    }
  }

  public fun insert(
    userId: UserId,
    repositoryId: RepositoryId,
    subscribedAt: LocalDateTime,
  ): Unit {
    driver.execute(-2042935346, """
        |INSERT INTO subscriptions (user_id, repository_id, subscribed_at)
        |VALUES (?, ?, ?)
        |ON CONFLICT DO NOTHING
        """.trimMargin(), 3) {
          check(this is JdbcPreparedStatement)
          bindLong(0, subscriptionsAdapter.user_idAdapter.encode(userId))
          bindLong(1, subscriptionsAdapter.repository_idAdapter.encode(repositoryId))
          bindObject(2, subscribedAt)
        }
    notifyQueries(-2042935346) { emit ->
      emit("subscriptions")
    }
  }

  private inner class FindAllQuery<out T : Any>(
    public val userId: UserId,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("repositories", "subscriptions"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("repositories", "subscriptions"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(-1716837709, """
    |SELECT repositories.owner,
    |       repositories.repository,
    |       subscriptions.subscribed_at
    |FROM repositories
    |INNER JOIN subscriptions ON subscriptions.repository_id = repositories.id
    |WHERE subscriptions.user_id = ?
    """.trimMargin(), mapper, 1) {
      check(this is JdbcPreparedStatement)
      bindLong(0, subscriptionsAdapter.user_idAdapter.encode(userId))
    }

    public override fun toString(): String = "Subscriptions.sq:findAll"
  }

  private inner class FindSubscribersQuery<out T : Any>(
    public val repositoryOwner: Owner,
    public val repositoryName: Repo,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("subscriptions", "repositories"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("subscriptions", "repositories"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(-1393674211, """
    |SELECT subscriptions.user_id
    |FROM subscriptions
    |INNER JOIN repositories ON subscriptions.repository_id = repositories.id
    |WHERE repositories.owner = ?
    |AND   repositories.repository = ?
    """.trimMargin(), mapper, 2) {
      check(this is JdbcPreparedStatement)
      bindString(0, repositoriesAdapter.ownerAdapter.encode(repositoryOwner))
      bindString(1, repositoriesAdapter.repositoryAdapter.encode(repositoryName))
    }

    public override fun toString(): String = "Subscriptions.sq:findSubscribers"
  }
}
