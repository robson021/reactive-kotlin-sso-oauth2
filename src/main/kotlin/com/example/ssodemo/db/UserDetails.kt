package com.example.ssodemo.db

import org.springframework.data.annotation.Id

data class UserDetails(
    @Id val id: String,
    val name: String,
    val email: String,
    val lastUpdate: Long = -1L
)
