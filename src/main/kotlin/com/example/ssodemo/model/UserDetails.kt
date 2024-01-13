package com.example.ssodemo.model

interface UserDetails {
    val id: String
    val name: String
    val avatar: String
    val scope: String
    val oauthProvider: OauthProvider
}

data class GithubUser(
    override val id: String,
    override val name: String,
    override val avatar: String,
    override val scope: String,
    override val oauthProvider: OauthProvider,
) : UserDetails

data class GoogleUser(
    override val id: String,
    override val name: String,
    override val avatar: String,
    override val scope: String,
    override val oauthProvider: OauthProvider,
    val email: String,
    val token: String,
) : UserDetails

class UserFactory {
    companion object Factory {
        fun fromGitHub(attr: Map<String, Any>, scope: String) = GithubUser(
            attr["login"] as String,
            attr["name"] as String,
            attr["avatar_url"] as String,
            scope,
            OauthProvider.GITHUB,
        )
        fun fromGoogle(attr: Map<String, Any>, scope: String, email: String, token: String) = GoogleUser(
            attr["sub"] as String,
            attr["name"] as String,
            attr["picture"] as String,
            scope,
            OauthProvider.GOOGLE,
            email,
            token,
        )
    }
}


enum class OauthProvider {
    GOOGLE,
    GITHUB,
    FACEBOOK,
}
