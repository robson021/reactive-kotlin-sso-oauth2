package com.example.ssodemo.model

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

interface UserDetails {
    val id: String
    val name: String
    val avatar: String
    val scope: List<String>
    val oauthProvider: OauthProvider
}

data class GithubUser(
    override val id: String,
    override val name: String,
    override val avatar: String,
    override val scope: List<String>,
    override val oauthProvider: OauthProvider,
) : UserDetails

data class GoogleUser(
    override val id: String,
    override val name: String,
    override val avatar: String,
    override val scope: List<String>,
    override val oauthProvider: OauthProvider,
    val email: String,
) : UserDetails

class UserFactory {
    companion object Factory {
        fun fromGitHub(attr: Map<String, Any>, scope: List<String>) = GithubUser(
            attr["login"] as String,
            attr["name"] as String,
            attr["avatar_url"] as String,
            scope,
            OauthProvider.GITHUB,
        )

        fun fromGoogle(attr: Map<String, Any>, scope: List<String>, email: String) = GoogleUser(
            attr["sub"] as String,
            attr["name"] as String,
            attr["picture"] as String,
            scope,
            OauthProvider.GOOGLE,
            email,
        )
    }
}

enum class OauthProvider {
    GOOGLE,
    GITHUB,
    FACEBOOK;

    companion object {
        fun fromString(provider: String) = when (provider.lowercase()) {
            "google" -> GOOGLE
            "github" -> GITHUB
            else -> throw UnsupportedOauthProvider("Unsupported provider $provider")
        }
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UnsupportedOauthProvider(override val message: String) : RuntimeException()
