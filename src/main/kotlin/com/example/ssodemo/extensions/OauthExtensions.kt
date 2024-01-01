package com.example.ssodemo.extensions

import com.example.ssodemo.db.UserDetails
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken

fun OAuth2AuthenticationToken.userDetails(): UserDetails {
    val attr = this.principal.attributes
    return UserDetails(
        attr["sub"] as String,
        attr["name"] as String,
        attr["email"] as String,
    )
}
