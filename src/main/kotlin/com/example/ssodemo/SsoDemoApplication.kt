package com.example.ssodemo

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
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


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
fun initializer(connectionFactory: ConnectionFactory?) = ConnectionFactoryInitializer().apply {
    setConnectionFactory(connectionFactory!!)
    setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
}

@RestController
class RestController(private val service: UserService) {

    @GetMapping("/")
    suspend fun userDetails(token: OAuth2AuthenticationToken) = token.userDetails()

    @GetMapping("/principal")
    suspend fun principal(token: OAuth2AuthenticationToken) = token.principal!!

    @GetMapping("/clients")
    suspend fun listClients() = service.listClients()

    @GetMapping("/users")
    suspend fun listUsers() = service.allUsers()

    @GetMapping("/db-user")
    suspend fun cachedUser(token: OAuth2AuthenticationToken) = service.getUser(token.userDetails().id) ?: "User not found."

    @GetMapping("/save-user")
    suspend fun getUserCustomField(token: OAuth2AuthenticationToken) = service.saveOrUpdateUser(token.userDetails())

}
