package com.training.xebia.functional.routes

import com.training.xebia.functional.client.GithubRepoClient
import com.training.xebia.functional.env.Env
import com.training.xebia.functional.model.RepoStatusResponse
import com.training.xebia.functional.model.Status
import com.training.xebia.functional.model.ValidateRepoResponse
import guru.zoroark.tegral.openapi.ktor.describe
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/validaterepo")
class ValidateRepo {

    @Serializable
    @Resource("/{owner}/{repo}")
    class Request(val parent: ValidateRepo, val owner: String, val repo: String)
}

fun Routing.validateRepoRoute(githubEnv: Env.GithubEnv) {
    get<ValidateRepo.Request> { req ->
        val client = GithubRepoClient(githubEnv)
        val repositoryExists = client.repositoryExists(req.owner, req.repo)

        repositoryExists.onRight {
            val reachable = RepoStatusResponse.Reachable(req.owner, req.repo, it)

            call.respond(
                status = HttpStatusCode.OK,
                ValidateRepoResponse(Status.Healthy(), reachable)
            )
        }
        repositoryExists.onLeft {
            val notAvailable = RepoStatusResponse.NotAvailable(req.owner, req.repo, false, it.toString())
            call.respond(
                status = HttpStatusCode.OK,
                ValidateRepoResponse(Status.Unhealthy(503, null), notAvailable)
            )
        }
    } describe {
        description = "Check if a Github Repository exists"
    }
}
