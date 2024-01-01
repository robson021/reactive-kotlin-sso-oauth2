package com.example.ssodemo

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repo: InMemoryReactiveClientRegistrationRepository
) {
    suspend fun listClients(): List<ClientRegistration> = repo.map { it }
}
