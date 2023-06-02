package com.training.xebia.functional.client

import arrow.core.Either
import arrow.core.continuations.either
import arrow.fx.coroutines.autoCloseable
import com.training.xebia.functional.env.Env
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import mu.KLogger
import mu.KotlinLogging

class GithubRepoClient(private val githubEnv: Env.GithubEnv) {

    private val logger: KLogger = KotlinLogging.logger { }
    private var client: arrow.fx.coroutines.Resource<HttpClient> = autoCloseable {
        HttpClient {
            install(Resources)
            install(HttpCache)
            install(DefaultRequest) { url(githubEnv.uri) }
        }
    }

    @Serializable
    @Resource("/repos/{owner}/{repo}")
    class Repo(val owner: String, val repo: String)

    suspend fun repositoryExists(owner: String, name: String): Either<GithubError, Boolean> = either {

        val response = client.use {
            it.get(Repo(owner, name)) {
                headers {
                    githubEnv.token?.let { token -> headers.append("Authorization", "Bearer $token") }
                    accept(ContentType.parse("application/vnd.github+json"))
                    contentType(ContentType.parse("application/json"))
                    headers.append("X-GitHub-Api-Version", "2022-11-28")
                    expectSuccess = false
                }
            }
        }

        when (response.status) {
            HttpStatusCode.OK -> true
            HttpStatusCode.NotModified -> true
            HttpStatusCode.NotFound -> false
            else -> {
                logger.info { "Unexpected status from GitHub call, description: ${response.status.description}" }
                shift(GithubError(response.status))
            }
        }
    }
}

@JvmInline
value class GithubError(val statusCode: HttpStatusCode)
