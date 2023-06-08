package com.training.xebia.functional.persistence.user

import app.cash.sqldelight.ExecutableQuery
import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcCursor
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.Users

public class UsersQueries(
    driver: SqlDriver,
    private val usersAdapter: Users.Adapter,
) : TransacterImpl(driver) {
  public fun insert(slackUserId: SlackUserId): ExecutableQuery<UserId> = InsertQuery(slackUserId) {
      cursor ->
    check(cursor is JdbcCursor)
    usersAdapter.idAdapter.decode(cursor.getLong(0)!!)
  }

  public fun <T : Any> findSlackUser(slackUserId: SlackUserId, mapper: (id: UserId,
      slack_user_id: SlackUserId) -> T): Query<T> = FindSlackUserQuery(slackUserId) { cursor ->
    check(cursor is JdbcCursor)
    mapper(
      usersAdapter.idAdapter.decode(cursor.getLong(0)!!),
      usersAdapter.slackUserIdAdapter.decode(cursor.getString(1)!!)
    )
  }

  public fun findSlackUser(slackUserId: SlackUserId): Query<Users> = findSlackUser(slackUserId) {
      id, slack_user_id ->
    Users(
      id,
      slack_user_id
    )
  }

  public fun <T : Any> find(userId: UserId, mapper: (id: UserId, slack_user_id: SlackUserId) -> T):
      Query<T> = FindQuery(userId) { cursor ->
    check(cursor is JdbcCursor)
    mapper(
      usersAdapter.idAdapter.decode(cursor.getLong(0)!!),
      usersAdapter.slackUserIdAdapter.decode(cursor.getString(1)!!)
    )
  }

  public fun find(userId: UserId): Query<Users> = find(userId) { id, slack_user_id ->
    Users(
      id,
      slack_user_id
    )
  }

  public fun <T : Any> findUsers(userId: Collection<UserId>, mapper: (id: UserId,
      slack_user_id: SlackUserId) -> T): Query<T> = FindUsersQuery(userId) { cursor ->
    check(cursor is JdbcCursor)
    mapper(
      usersAdapter.idAdapter.decode(cursor.getLong(0)!!),
      usersAdapter.slackUserIdAdapter.decode(cursor.getString(1)!!)
    )
  }

  public suspend fun findUsers(userId: Collection<UserId>): Query<Users> = findUsers(userId) { id,
                                                                                       slack_user_id ->
    Users(
      id,
      slack_user_id
    )
  }

  public fun deleteUser(userId: UserId): Unit {
    driver.execute(-399472835, """
        |DELETE FROM users
        |WHERE id = ?
        """.trimMargin(), 1) {
          check(this is JdbcPreparedStatement)
          bindLong(0, usersAdapter.idAdapter.encode(userId))
        }
    notifyQueries(-399472835) { emit ->
      emit("subscriptions")
      emit("users")
    }
  }

  private inner class InsertQuery<out T : Any>(
    public val slackUserId: SlackUserId,
    mapper: (SqlCursor) -> T,
  ) : ExecutableQuery<T>(mapper) {
    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(-1478566560, """
    |INSERT INTO users(slack_user_id)
    |VALUES (?)
    |RETURNING id
    """.trimMargin(), mapper, 1) {
      check(this is JdbcPreparedStatement)
      bindString(0, usersAdapter.slackUserIdAdapter.encode(slackUserId))
    }

    public override fun toString(): String = "Users.sq:insert"
  }

  private inner class FindSlackUserQuery<out T : Any>(
    public val slackUserId: SlackUserId,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("users"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("users"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(1924312379, """
    |SELECT id, slack_user_id
    |FROM users
    |WHERE slack_user_id = ?
    """.trimMargin(), mapper, 1) {
      check(this is JdbcPreparedStatement)
      bindString(0, usersAdapter.slackUserIdAdapter.encode(slackUserId))
    }

    public override fun toString(): String = "Users.sq:findSlackUser"
  }

  private inner class FindQuery<out T : Any>(
    public val userId: UserId,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("users"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("users"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> =
        driver.executeQuery(1357024800, """
    |SELECT id, slack_user_id
    |FROM users
    |WHERE id = ?
    """.trimMargin(), mapper, 1) {
      check(this is JdbcPreparedStatement)
      bindLong(0, usersAdapter.idAdapter.encode(userId))
    }

    public override fun toString(): String = "Users.sq:find"
  }

  private inner class FindUsersQuery<out T : Any>(
    public val userId: Collection<UserId>,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    public override fun addListener(listener: Query.Listener): Unit {
      driver.addListener(listener, arrayOf("users"))
    }

    public override fun removeListener(listener: Query.Listener): Unit {
      driver.removeListener(listener, arrayOf("users"))
    }

    public override fun <R> execute(mapper: (SqlCursor) -> R): QueryResult<R> {
      val userIdIndexes = createArguments(count = userId.size)
      return driver.executeQuery(null, """
          |SELECT id, slack_user_id
          |FROM users
          |WHERE id IN $userIdIndexes
          """.trimMargin(), mapper, userId.size) {
            check(this is JdbcPreparedStatement)
            userId.forEachIndexed { index, userId_ ->
              bindLong(index, usersAdapter.idAdapter.encode(userId_))
            }
          }
    }

    public override fun toString(): String = "Users.sq:findUsers"
  }
}
