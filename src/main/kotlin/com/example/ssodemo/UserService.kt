package com.example.ssodemo

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repo: InMemoryReactiveClientRegistrationRepository
) {
    suspend fun getUserInfo(token: OAuth2AuthenticationToken): Map<String, Any> {
        val attr = token.principal.attributes

        data class Details(val sub: String, val name: String, val email: String) {
            // todo: store
        }
        return attr
    }

    suspend fun listClients(): List<ClientRegistration> = repo.map { it }
}
