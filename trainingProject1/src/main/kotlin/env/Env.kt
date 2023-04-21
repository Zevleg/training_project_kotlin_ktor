package env

data class Config(val env: String, val server: Server, val postgres: Postgres)
data class Server(val host: String, val port: Int)
data class Postgres(val url: String, val username: String, val password: String)
