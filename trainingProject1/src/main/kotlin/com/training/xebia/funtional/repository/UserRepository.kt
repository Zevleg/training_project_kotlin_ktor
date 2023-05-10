package com.training.xebia.funtional.repository

import com.training.xebia.funtional.model.User
import com.training.xebia.funtional.model.Users

val user1 = User(1, "Daniel", 34)
val user2 = User(2, "Khate", 30)
val user3 = User(3, "Laura", 32)
val user4 = User(4, "Vilma", 63)
val user5 = User(5, "Melba", 82)

val usersRepository = Users(listOf(user1, user2, user3, user4, user5))