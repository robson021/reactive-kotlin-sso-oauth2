package com.example.ssodemo

import com.example.ssodemo.db.UserDetails
import com.example.ssodemo.extensions.userDetails
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


@SpringBootApplication
@EnableR2dbcRepositories
class SsoDemoApplication

fun main(args: Array<String>) {
    runApplication<SsoDemoApplication>(*args)
}

@Bean
fun initializer(connectionFactory: ConnectionFactory?): ConnectionFactoryInitializer {
    val initializer = ConnectionFactoryInitializer()
    initializer.setConnectionFactory(connectionFactory!!)
    initializer.setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
    return initializer
}

@RestController
class RestController(private val service: UserService) {

    @GetMapping("/")
    suspend fun homePage() = "hello"

    @GetMapping("/clients")
    suspend fun listClients(): List<ClientRegistration> = service.listClients()

    @GetMapping("/who-am-i")
    suspend fun principal(token: OAuth2AuthenticationToken): OAuth2User = token.principal

    @GetMapping("/user-details")
    suspend fun userDetails(token: OAuth2AuthenticationToken): UserDetails = token.userDetails()

    @GetMapping("/users")
    suspend fun listUsers(): List<UserDetails> = service.allUsers()

    @GetMapping("/last-update")
    suspend fun lastUpdate(token: OAuth2AuthenticationToken): LocalDateTime {
        val timestamp = service.lastUpdate(token.userDetails())
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            TimeZone.getDefault().toZoneId(),
        )
    }

}
