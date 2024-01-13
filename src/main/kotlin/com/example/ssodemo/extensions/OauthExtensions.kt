package com.example.ssodemo.extensions

import com.example.ssodemo.model.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

fun OAuth2AuthenticationToken.userDetails(): UserDetails {
    val attr = this.principal.attributes

    val email = attr["email"]
    val isGoogle = email != null && ((email as String).contains("@gmail."))

    return when {
        isGoogle -> UserDetails.fromGoogle(attr)
        else -> UserDetails.fromGitHub(attr)
    }

}
