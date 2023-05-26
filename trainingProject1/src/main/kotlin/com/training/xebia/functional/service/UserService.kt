package com.training.xebia.functional.service

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.continuations.ensureNotNull
import com.training.xebia.functional.domain.UserId
import com.training.xebia.functional.domain.UserNotFound
import com.training.xebia.functional.persistence.user.Users
import com.training.xebia.functional.persistence.user.UsersQueries
import mu.KotlinLogging

interface UserService{
    suspend fun find(userId: UserId): Either<UserNotFound, List<Users>>
}

fun UserService(
    users: UsersQueries
): UserService = SqlDelightUsersService(users)

private class SqlDelightUsersService(private val users: UsersQueries
) : UserService {
    private val logger = KotlinLogging.logger { }

    override suspend fun find(userId: UserId): Either<UserNotFound, List<Users>> =
        either {
            val user = users.find(userId)
            ensureNotNull(user) { UserNotFound(userId) }
            user.executeAsList()
        }
}
