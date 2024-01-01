package com.example.ssodemo

import com.example.ssodemo.extensions.userDetails
import com.example.ssodemo.model.UserDetails
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class SsoDemoApplication

fun main(args: Array<String>) {
    runApplication<SsoDemoApplication>(*args)
}

@RestController
class RestController(private val service: UserService) {

    @GetMapping("/")
    suspend fun homePage() = "hello"

    @GetMapping("/list")
    suspend fun list(): List<ClientRegistration> = service.listClients()

    @GetMapping("/who-am-i")
    suspend fun principal(token: OAuth2AuthenticationToken): OAuth2User = token.principal

    @GetMapping("/user-details")
    suspend fun userDetails(token: OAuth2AuthenticationToken): UserDetails = token.userDetails()

}
