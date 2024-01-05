package com.example.ssodemo.model

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames
import org.springframework.stereotype.Component

interface OAuthCredentials {
    fun getClientRegistration(): ClientRegistration
}

private const val DEFAULT_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}"

@Component
data class GoogleAuth(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}") val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}") val clientSecret: String,
) : OAuthCredentials {
    override fun getClientRegistration() = ClientRegistration
        .withRegistrationId("google")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .redirectUri(DEFAULT_REDIRECT_URL)
        .scope("openid", "profile", "email")
        .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
        .tokenUri("https://www.googleapis.com/oauth2/v4/token")
        .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
        .userNameAttributeName(IdTokenClaimNames.SUB)
        .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
        .clientName("Google")
        .build()!!
}

@Component
data class FacebookAuth(
    @Value("\${spring.security.oauth2.client.registration.facebook.client-id}") val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.facebook.client-secret}") val clientSecret: String,
) : OAuthCredentials {
    override fun getClientRegistration(): ClientRegistration = ClientRegistration
        .withRegistrationId("facebook")
        .clientId(clientId)
        .clientSecret(clientSecret)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
        .scope("public_profile", "email")
        .authorizationUri("https://www.facebook.com/v2.8/dialog/oauth")
        .tokenUri("https://graph.facebook.com/v2.8/oauth/access_token")
        .userInfoUri("https://graph.facebook.com/me?fields=id,name,email")
        .userNameAttributeName("id")
        .clientName("Facebook")
        // https://github.com/spring-projects/spring-security/blob/main/config/src/main/java/org/springframework/security/config/oauth2/client/CommonOAuth2Provider.java#L73
        // .redirectUri(DEFAULT_REDIRECT_URL)
        .build()!!
}
