package com.training.xebia.funtional.model

import arrow.optics.optics
import kotlinx.serialization.Serializable

@optics
@Serializable
data class Users(val users: List<User>) {
    companion object
}

@optics
@Serializable
data class User(val id: Int, val name: String, val age: Int) {
    companion object
}

@optics
@Serializable
data class UsersResponse(val users: List<User>) {
    companion object
}
