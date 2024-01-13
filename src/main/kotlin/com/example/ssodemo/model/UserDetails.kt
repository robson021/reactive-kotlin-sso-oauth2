package com.example.ssodemo.model

data class UserDetails(
    val id: String,
    val name: String,
    val email: String,
    val oauthProvider: OauthProvider,
) {
    companion object Factory {
        fun fromGoogle(attr: Map<String, Any>) = UserDetails(
            attr["sub"] as String,
            attr["name"] as String,
            attr["email"] as String,
            OauthProvider.GOOGLE,
        )
        fun fromGitHub(attr: Map<String, Any>) = UserDetails(
            attr["login"] as String,
            attr["name"] as String,
            "unknown_email",
            OauthProvider.GITHUB,
        )
    }
}

enum class OauthProvider {
    GOOGLE,
    GITHUB,
    FACEBOOK,
}
