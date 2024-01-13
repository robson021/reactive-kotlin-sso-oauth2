package com.example.ssodemo.db

import org.springframework.data.annotation.Id

data class UserDetails(
    @Id val id: String,
    val name: String,
    val email: String,
    val lastUpdate: Long = -1L
) {
    companion object Factory {
        fun fromGoogle(attr: Map<String, Any>) = UserDetails(
            attr["sub"] as String,
            attr["name"] as String,
            attr["email"] as String,
        )

        fun fromGitHub(attr: Map<String, Any>) = UserDetails(
            attr["login"] as String,
            attr["name"] as String,
            "unknown_email",
        )
    }
}
