package com.example.ssodemo.model

data class UserDetails(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String,
    val scope: String,
    val oauthProvider: OauthProvider,
) {
    companion object Factory {
        fun fromGoogle(attr: Map<String, Any>, scope: String) = UserDetails(
            attr["sub"] as String,
            attr["name"] as String,
            attr["email"] as String,
            attr["picture"] as String,
            scope,
            OauthProvider.GOOGLE,
        )

        fun fromGitHub(attr: Map<String, Any>, scope: String) = UserDetails(
            attr["login"] as String,
            attr["name"] as String,
            "unknown_email",
            attr["avatar_url"] as String,
            scope,
            OauthProvider.GITHUB,
        )
    }
}

enum class OauthProvider {
    GOOGLE,
    GITHUB,
    FACEBOOK,
}
