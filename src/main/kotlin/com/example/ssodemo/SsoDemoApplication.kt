package com.example.ssodemo

import com.example.ssodemo.extensions.getLogger
import com.example.ssodemo.extensions.logDebug
import com.example.ssodemo.extensions.userDetails
import com.example.ssodemo.model.OAuthCredentials
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.http.HttpStatus
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono


@SpringBootApplication
@EnableWebFluxSecurity
@EnableR2dbcRepositories
class SsoDemoApplication

fun main(args: Array<String>) {
    runApplication<SsoDemoApplication>(*args)
}

@Bean
fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
    http.authorizeExchange { authorize: ServerHttpSecurity.AuthorizeExchangeSpec ->
        authorize.anyExchange().authenticated()
    }.oauth2Login(Customizer.withDefaults())
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
fun initializer(connectionFactory: ConnectionFactory) = ConnectionFactoryInitializer()
    .apply {
        setConnectionFactory(connectionFactory)
        setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource("schema.sql")))
    }

@Component
@Profile("restrict-users")
@PropertySource(value = ["classpath:application-restricted-users.properties"])
class ApiPathFilter(
    @Value("\${app.allowed-users}") private val allowedUsers: Set<String>,
) : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.path
        if (!pathMatcher.matches(path)) {
            return chain.filter(exchange)
        }
        log.logDebug { "Filtering path: $path" }
        return exchange.getPrincipal<OAuth2AuthenticationToken>()
            .map { it.userDetails().id }
            .doOnNext {
                if (!allowedUsers.contains(it)) {
                    log.warn("Unauthorized user: $it")
                    throw ForbiddenException("$it is is not allowed for '$path'.")
                }
            }.flatMap {
                chain.filter(exchange)
            }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    class ForbiddenException(override val message: String) : RuntimeException()

    companion object {
        private val log by getLogger()
        private val pathMatcher = PathPatternParser().parse("/api/**")
    }
}

@RestController
class RestController(private val service: UserService) {

    @GetMapping("/")
    suspend fun userDetails(token: OAuth2AuthenticationToken) = token.userDetails()

    @GetMapping("/principal")
    suspend fun principal(token: OAuth2AuthenticationToken) = token.principal!!
}

@RestController
@RequestMapping("/api")
class ApiController(private val service: UserService) {

    @GetMapping("/clients")
    suspend fun listClients() = service.listOauthClients()

    @GetMapping("/users")
    suspend fun listUsers() = service.allUsers()

    @GetMapping("/db-user")
    suspend fun cachedUser(token: OAuth2AuthenticationToken) = service.getUser(token.userDetails().id) ?: "User not found."

    @GetMapping("/save-user")
    suspend fun getUserCustomField(token: OAuth2AuthenticationToken) = service.saveOrUpdateUser(token.userDetails())
}
