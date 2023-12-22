package com.example.ssodemo

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter


@Configuration
@EnableWebFluxSecurity
class WebConfig {


    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { authorize: AuthorizeExchangeSpec ->
                authorize
                    .pathMatchers("/").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2Login(withDefaults())
        return http.build()
    }

    @Bean
    fun clientRegistrationRepository(env: Environment): ReactiveClientRegistrationRepository {
        val clientId = env.getRequiredProperty("spring.security.oauth2.client.registration.google.client-id")
        val clientSecret = env.getRequiredProperty("spring.security.oauth2.client.registration.google.client-secret")

        log.debug("Google credentials: $clientId / $clientSecret")

        val googleClient = ClientRegistration.withRegistrationId("google")
            .clientId(clientId)
            .clientSecret(clientSecret)
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
    fun restRoutes(baseHandler: RestHandler): RouterFunction<ServerResponse> = coRouter {
        GET("/") { baseHandler.homePage() }
        GET("/test") { baseHandler.testPage() }
    }

    companion object {
        private val log = LoggerFactory.getLogger(WebConfig::class.java)
    }
}
