package com.example.ssodemo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class GoogleAuthCredentials(
    @Value("\${spring.security.oauth2.client.registration.google.client-id}") val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.google.client-secret}") val clientSecret: String,
)
