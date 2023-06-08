package com.training.xebia.functional.persistence.user

import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.Users
import kotlin.time.Duration.Companion.seconds

interface UserPersistence {
    suspend fun findUsers(userIds: List<UserId>): List<Users>
    suspend fun find(userId: UserId): Users?
    suspend fun findSlackUser(slackUserId: SlackUserId): Users?
    suspend fun getOrInsertSlackUser(slackUserId: SlackUserId): Users
    suspend fun deleteSlackUser(slackUserId: SlackUserId)
    suspend fun deleteUser(userId: UserId)
}

fun UserPersistence(
    userQueries: UsersQueries
): UserPersistence = UserPersistenceImpl(userQueries)

private class UserPersistenceImpl(private val userQueries: UsersQueries) : UserPersistence {


    suspend fun <A> resilient(schedule: Schedule<Throwable, *>, f: suspend () -> A): A =
        schedule.retry { setCircuitBreaker().protectOrThrow(f) }

    override suspend fun findUsers(userIds: List<UserId>): List<Users> =
        Schedule.recurs<Throwable>(5).retry {
            setCircuitBreaker().protectOrThrow(userQueries.findUsers(userIds).executeAsList())
        }


    override suspend fun find(userId: UserId): Users? =
        userQueries.find(userId, ::Users).executeAsOneOrNull()

    override suspend fun findSlackUser(slackUserId: SlackUserId): Users? =
        userQueries.findSlackUser(slackUserId, ::Users).executeAsOneOrNull()

    override suspend fun getOrInsertSlackUser(slackUserId: SlackUserId): Users =
        userQueries.transactionWithResult {
            userQueries.findSlackUser(slackUserId, ::Users).executeAsOneOrNull()
                ?: Users(userQueries.insert(slackUserId).executeAsOne(), slackUserId)
        }

    override suspend fun deleteSlackUser(slackUserId: SlackUserId): Unit =
        userQueries.transactionWithResult {
            val user = userQueries.findSlackUser(slackUserId, ::Users).executeAsOneOrNull()
            userQueries.deleteUser(user!!.id)
        }

    override suspend fun deleteUser(userId: UserId): Unit =
        userQueries.deleteUser(userId)
}

suspend fun setCircuitBreaker(): CircuitBreaker {
    return CircuitBreaker.of(
        maxFailures = 2,
        resetTimeout = 2.seconds,
        exponentialBackoffFactor = 2.0, // enable exponentialBackoffFactor
        maxResetTimeout = 60.seconds, // limit exponential back-off time
    )
}
