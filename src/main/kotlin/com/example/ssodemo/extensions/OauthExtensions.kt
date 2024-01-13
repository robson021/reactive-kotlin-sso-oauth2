package com.example.ssodemo.extensions

import com.example.ssodemo.model.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.DefaultOAuth2User

fun OAuth2AuthenticationToken.userDetails(): UserDetails {
    val principal = this.principal as DefaultOAuth2User
    val attr = principal.attributes
    val scope = principal.authorities.last().authority

    val email = attr["email"]
    val isGoogle = email != null && ((email as String).contains("@gmail."))

    return when {
        isGoogle -> UserDetails.fromGoogle(attr, scope)
        else -> UserDetails.fromGitHub(attr, scope)
    }

}
