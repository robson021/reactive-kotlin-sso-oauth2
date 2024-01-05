package com.example.ssodemo

import com.example.ssodemo.db.UserDetails
import com.example.ssodemo.extensions.userDetails
import com.example.ssodemo.model.OAuthCredentials
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDateTime
import java.util.*


@SpringBootApplication
@EnableWebFluxSecurity
@EnableR2dbcRepositories
class SsoDemoApplication

fun main(args: Array<String>) {
    runApplication<SsoDemoApplication>(*args)
}

@Bean
fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    http
        .authorizeExchange { authorize: ServerHttpSecurity.AuthorizeExchangeSpec ->
            authorize.anyExchange().authenticated()
        }
        .oauth2Login(Customizer.withDefaults())
    return http.build()
}

@Bean
fun clientRegistrationRepository(
    googleAuth: OAuthCredentials,
    githubAuth: OAuthCredentials,
    facebookAuth: OAuthCredentials,
) = InMemoryReactiveClientRegistrationRepository(
    googleAuth.getClientRegistration(),
    githubAuth.getClientRegistration(),
    facebookAuth.getClientRegistration(),
)

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
    suspend fun userDetails(token: OAuth2AuthenticationToken): UserDetails = token.userDetails()

    @GetMapping("/principal")
    suspend fun principal(token: OAuth2AuthenticationToken): OAuth2User = token.principal

    @GetMapping("/clients")
    suspend fun listClients(): List<ClientRegistration> = service.listClients()

    @GetMapping("/users")
    suspend fun listUsers(): List<UserDetails> = service.allUsers()

    @GetMapping("/cache")
    suspend fun cachedUser(token: OAuth2AuthenticationToken): UserDetails = service.getCachedUser(token.userDetails())

    @GetMapping("/last-update")
    suspend fun lastUpdate(token: OAuth2AuthenticationToken): LocalDateTime {
        val timestamp = service.lastUpdate(token.userDetails())
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            TimeZone.getDefault().toZoneId(),
        )
    }

}
