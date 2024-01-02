package com.example.ssodemo

import com.example.ssodemo.db.UserDetails
import com.example.ssodemo.extensions.userDetails
import com.example.ssodemo.model.GoogleAuthCredentials
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
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
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
            authorize
                .pathMatchers("/").permitAll()
                .anyExchange().authenticated()
        }
        .oauth2Login(Customizer.withDefaults())
    return http.build()
}

@Bean
fun clientRegistrationRepository(googleAut: GoogleAuthCredentials): InMemoryReactiveClientRegistrationRepository {
    val googleClient = ClientRegistration.withRegistrationId("google")
        .clientId(googleAut.clientId)
        .clientSecret(googleAut.clientSecret)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
        .scope("openid", "profile", "email")
        .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
        .tokenUri("https://www.googleapis.com/oauth2/v4/token")
        .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
        .userNameAttributeName(IdTokenClaimNames.SUB)
        .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
        .clientName("Google")
        .build()
    return InMemoryReactiveClientRegistrationRepository(googleClient)
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
