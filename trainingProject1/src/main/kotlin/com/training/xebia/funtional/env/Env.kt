package com.training.xebia.funtional.env

class Env() {
    data class Config(
        val env: String,
        val server: Server,
        val postgres: Postgres,
        val github: GithubEnv
    )

    data class Server(val host: String, val port: Int)
    data class Postgres(val url: String, val username: String, val password: String)
    data class GithubEnv(val uri: String, val token: String?)
}
