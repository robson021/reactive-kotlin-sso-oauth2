package com.example.ssodemo

import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class RestHandler(
    private val repo: InMemoryReactiveClientRegistrationRepository
) {

    suspend fun homePage(): ServerResponse = Response.toPlainText("Hello world!")

    suspend fun listClients(): ServerResponse {
        data class ClientInfo(
            val clientId: String,
            val registrationId: String,
            val clientName: String,
            val scopes: Collection<String>,
        )

        val clients = repo.map {
            ClientInfo(it.clientId, it.registrationId, it.clientName, it.scopes)
        }
        return Response.toJson(clients)
    }
}
