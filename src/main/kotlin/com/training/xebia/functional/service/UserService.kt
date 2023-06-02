package com.training.xebia.functional.service

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.continuations.ensureNotNull
import com.training.xebia.functional.domain.*
import com.training.xebia.functional.persistence.user.UserPersistence
import mu.KotlinLogging

interface UserService{
    suspend fun findUsers(userIds: List<UserId>): Either<UserError, List<Users>>
    suspend fun find(userId: UserId): Either<UserError, Users>
    suspend fun findSlackUser(slackUserId: SlackUserId): Either<UserError, Users>
    suspend fun getOrCreate(slackUser: SlackUserId) : Either<UserError, Users>
    suspend fun deleteUser(userId: UserId)
}

fun UserService(
    userPersistence: UserPersistence
): UserService = UsersServiceImpl(userPersistence)

private class UsersServiceImpl(private val userPersistence: UserPersistence
) : UserService {
    private val logger = KotlinLogging.logger { }

    override suspend fun findUsers(userIds: List<UserId>): Either<UserError, List<Users>> =
        either {
            val users = userPersistence.findUsers(userIds)
            ensureNotNull(users) { UsersNotFound(userIds) }
        }

    override suspend fun find(userId: UserId): Either<UserError, Users> =
        either {
            val user = userPersistence.find(userId)
            ensureNotNull(user) { UserNotFound(userId) }
        }

    override suspend fun findSlackUser(slackUserId: SlackUserId): Either<UserError, Users> =
        either {
            val user = userPersistence.findSlackUser(slackUserId)
            ensureNotNull(user) { UserNotFound(null, slackUserId) }
        }

    override suspend fun getOrCreate(slackUser: SlackUserId) : Either<UserError, Users> =
        either {
            val user = userPersistence.getOrInsertSlackUser(slackUser)
            ensureNotNull(user) { UserGetOrCreateError(slackUser) }
        }

    override suspend fun deleteUser(userId: UserId) =
        userPersistence.deleteUser(userId)
}
