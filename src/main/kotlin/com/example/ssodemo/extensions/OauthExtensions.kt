package com.example.ssodemo.extensions

import com.example.ssodemo.model.OauthProvider
import com.example.ssodemo.model.UserDetails
import com.example.ssodemo.model.UserFactory
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

fun OAuth2AuthenticationToken.userDetails(): UserDetails {
    val attr = principal.attributes
    val scope = principal.authorities.map { it.authority }

    return when (OauthProvider.fromString(authorizedClientRegistrationId)) {
        OauthProvider.GOOGLE -> UserFactory.fromGoogle(attr, scope, attr["email"] as String)
        OauthProvider.GITHUB -> UserFactory.fromGitHub(attr, scope)
        OauthProvider.FACEBOOK -> TODO()
    }
}