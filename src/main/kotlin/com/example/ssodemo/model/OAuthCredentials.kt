package com.example.ssodemo.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.stereotype.Component

interface OAuthCredentials {
    fun getClientRegistration(): ClientRegistration
}


@Component
data class GoogleAuth(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}") val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}") val clientSecret: String,
) : OAuthCredentials {
    override fun getClientRegistration() = CommonOAuth2Provider.GOOGLE
        .getBuilder("google")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .build()!!
}

@Component
data class GithubAuth(
    @Value("\${spring.security.oauth2.client.registration.github.client-id}") val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.github.client-secret}") val clientSecret: String,
) : OAuthCredentials {
    override fun getClientRegistration() = CommonOAuth2Provider.GITHUB
        .getBuilder("github")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .build()!!
}

@Component
data class FacebookAuth(
    @Value("\${spring.security.oauth2.client.registration.facebook.client-id}") val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.facebook.client-secret}") val clientSecret: String,
) : OAuthCredentials {
    override fun getClientRegistration() = CommonOAuth2Provider.FACEBOOK
        .getBuilder("facebook")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .build()!!
}
// https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/oauth2/client/CommonOAuth2Provider.java#L73
