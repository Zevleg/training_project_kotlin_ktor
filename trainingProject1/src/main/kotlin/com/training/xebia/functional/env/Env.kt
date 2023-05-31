package com.training.xebia.functional.env

class Env() {
    data class Config(
        val env: String,
        val server: Server,
        val postgres: Postgres,
        val postgresContainer: PostgresContainer,
        val github: GithubEnv
    )

    data class Server(val host: String, val port: Int)
    data class Postgres(
        val url: String,
        val serverName: String,
        val portNumber: String,
        val databaseName: String,
        val username: String,
        val password: String
    ){
        val driver: String = "org.postgresql.Driver"
    }

    data class PostgresContainer(
        val url: String,
        val username: String,
        val password: String
    ){
        val driver: String = "org.postgresql.Driver"
    }

    data class GithubEnv(val uri: String, val token: String?)
}
