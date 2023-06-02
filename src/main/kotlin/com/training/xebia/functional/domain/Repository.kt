package com.training.xebia.functional.domain

import app.cash.sqldelight.ColumnAdapter
import arrow.optics.optics
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class RepositoryId(val id: Long){
    companion object {
        val adapter: ColumnAdapter<RepositoryId, Long> = object : ColumnAdapter<RepositoryId, Long> {
            override fun decode(databaseValue: Long): RepositoryId = RepositoryId(databaseValue)
            override fun encode(value: RepositoryId): Long = value.id
        }
    }
}

@JvmInline
@Serializable
value class Owner(val owner: String){
    companion object {
        val adapter: ColumnAdapter<Owner, String> = object : ColumnAdapter<Owner, String> {
            override fun decode(databaseValue: String): Owner = Owner(databaseValue)
            override fun encode(value: Owner): String = value.owner
        }
    }
}

@JvmInline
@Serializable
value class Repo(val repo: String){
    companion object {
        val adapter: ColumnAdapter<Repo, String> = object : ColumnAdapter<Repo, String> {
            override fun decode(databaseValue: String): Repo = Repo(databaseValue)
            override fun encode(value: Repo): String = value.repo
        }
    }
}

@optics
@Serializable
data class Repository(val id: RepositoryId, val owner: Owner, val repository: Repo) {
    companion object
}

@optics
@Serializable
data class Repositories(val repositories: List<Repository>) {
    companion object
}
