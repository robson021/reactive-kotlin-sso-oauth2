package com.example.ssodemo.db

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("USERS")
data class User(
    @Id val id: String,
    val name: String,
    val email: String,
    val customField: String,
)
