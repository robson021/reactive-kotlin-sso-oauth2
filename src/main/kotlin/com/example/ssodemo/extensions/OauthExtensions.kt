package com.example.ssodemo.extensions

import com.example.ssodemo.db.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

fun OAuth2AuthenticationToken.userDetails(): UserDetails {
    val attr = this.principal.attributes

    val email = attr["email"]
    val isGoogle = email != null && ((email as String).contains("@gmail."))

    val id: String = when {
        isGoogle -> "sub"
        else -> "login"
    }

    val mail: String = if (email == null) {
        "unknown_email"
    } else {
        email as String
    }

    return UserDetails(
        attr[id] as String,
        attr["name"] as String,
        mail,
    )
}
