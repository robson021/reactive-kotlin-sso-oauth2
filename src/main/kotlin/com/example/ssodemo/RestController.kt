package com.example.ssodemo

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController(private val service: UserService) {

    @GetMapping("/")
    suspend fun homePage() = "hello"

    @GetMapping("/list")
    suspend fun list(): List<ClientRegistration> = service.listClients()

    @GetMapping("/who-am-i")
    suspend fun userInfo(token: OAuth2AuthenticationToken): Map<String, Any> {
        return service.getUserInfo(token)
    }

}
