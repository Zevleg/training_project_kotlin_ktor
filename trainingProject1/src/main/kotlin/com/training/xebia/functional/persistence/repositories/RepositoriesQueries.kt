package com.training.xebia.functional.persistence.repositories

import app.cash.sqldelight.ExecutableQuery
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

public class RepositoriesQueries(
    driver: SqlDriver,
    private val repositoriesAdapter: Repositories.Adapter,
) : TransacterImpl(driver) {
  public fun insert(repositoryOwner: Owner, repositoryName: Repo): ExecutableQuery<RepositoryId> =
      InsertQuery(repositoryOwner, repositoryName) { cursor ->
    check(cursor is JdbcCursor)
    repositoriesAdapter.idAdapter.decode(cursor.getLong(0)!!)
  }

  public fun selectId(owner: Owner, repository: Repo): Query<RepositoryId> = SelectIdQuery(owner,
      repository) { cursor ->
    check(cursor is JdbcCursor)
    repositoriesAdapter.idAdapter.decode(cursor.getLong(0)!!)
  }

  private inner class InsertQuery<out T : Any>(
    public val repositoryOwner: Owner,
    public val repositoryName: Repo,
    mapper: (SqlCursor) -> T,
  ) : ExecutableQuery<T>(mapper) {
    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(-2142235970, """
    |INSERT INTO repositories(owner, repository)
    |VALUES (?, ?)
    |ON CONFLICT DO NOTHING
    |RETURNING id
    """.trimMargin(), mapper, 2) {
      check(this is JdbcPreparedStatement)
      bindString(0, repositoriesAdapter.ownerAdapter.encode(repositoryOwner))
      bindString(1, repositoriesAdapter.repositoryAdapter.encode(repositoryName))
    }

    public override fun toString(): String = "Repositories.sq:insert"
  }

  private inner class SelectIdQuery<out T : Any>(
    public val owner: Owner,
    public val repository: Repo,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("repositories"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("repositories"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(-749645316, """
    |SELECT id
    |FROM repositories
    |WHERE owner = ? AND repository = ?
    """.trimMargin(), mapper, 2) {
      check(this is JdbcPreparedStatement)
      bindString(0, repositoriesAdapter.ownerAdapter.encode(owner))
      bindString(1, repositoriesAdapter.repositoryAdapter.encode(repository))
    }

    public override fun toString(): String = "Repositories.sq:selectId"
  }
}
