package com.training.xebia.functional.persistence.user

import arrow.fx.coroutines.CircuitBreaker
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.retry
import com.training.xebia.functional.domain.SlackUserId
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.Users
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

interface UserPersistence {
    suspend fun findUsers(userIds: List<UserId>): List<Users>
    suspend fun find(userId: UserId): Users?
    suspend fun findSlackUser(slackUserId: SlackUserId): Users?
    suspend fun getOrInsertSlackUser(slackUserId: SlackUserId): Users
    suspend fun deleteSlackUser(slackUserId: SlackUserId)
    suspend fun deleteUser(userId: UserId)
}

fun UserPersistence(
    circuitBreaker: CircuitBreaker,
    userQueries: UsersQueries,
    retryPolicy: Schedule<Throwable, Unit> = defaultRetrySchedule
): UserPersistence = UserPersistenceImpl(circuitBreaker, userQueries, retryPolicy)

private const val DEFAULT_RETRY_COUNT = 3

@OptIn(ExperimentalTime::class)
private val defaultRetrySchedule: Schedule<Throwable, Unit> =
    Schedule.recurs<Throwable>(DEFAULT_RETRY_COUNT)
        .and(Schedule.exponential(1.seconds))
        .void()

private class UserPersistenceImpl(
    private val circuitBreaker: CircuitBreaker,
    private val userQueries: UsersQueries,
    private val retryPolicy: Schedule<Throwable, Unit>
) : UserPersistence {

    override suspend fun findUsers(userIds: List<UserId>): List<Users> =
        retryPolicy.retry {
            circuitBreaker.protectOrThrow { userQueries.findUsers(userIds).executeAsList() }
        }


    override suspend fun find(userId: UserId): Users? =
        retryPolicy.retry {
            circuitBreaker.protectOrThrow { userQueries.find(userId, ::Users).executeAsOneOrNull() }
        }

    override suspend fun findSlackUser(slackUserId: SlackUserId): Users? =
        retryPolicy.retry {
            circuitBreaker.protectOrThrow {
                userQueries.findSlackUser(slackUserId, ::Users).executeAsOneOrNull()
            }
        }

    override suspend fun getOrInsertSlackUser(slackUserId: SlackUserId): Users =
        retryPolicy.retry {
            circuitBreaker.protectOrThrow {
                userQueries.transactionWithResult {
                    userQueries.findSlackUser(slackUserId, ::Users).executeAsOneOrNull()
                        ?: Users(userQueries.insert(slackUserId).executeAsOne(), slackUserId)
                }
            }
        }

    override suspend fun deleteSlackUser(slackUserId: SlackUserId): Unit =
        retryPolicy.retry {
            circuitBreaker.protectOrThrow {
                userQueries.transactionWithResult {
                    val user = userQueries.findSlackUser(slackUserId, ::Users).executeAsOneOrNull()
                    userQueries.deleteUser(user!!.id)
                }
            }
        }

    override suspend fun deleteUser(userId: UserId): Unit =
        retryPolicy.retry {
            circuitBreaker.protectOrThrow { userQueries.deleteUser(userId) }
        }
}
