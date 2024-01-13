package com.example.ssodemo

import com.example.ssodemo.db.User
import com.example.ssodemo.db.UserRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.stereotype.Service

@Service
//@Transactional
class UserService(
    private val clientRegistrationRepository: InMemoryReactiveClientRegistrationRepository,
    private val userRepository: UserRepository,
    private val dbClient: DatabaseClient,
) {
    suspend fun listClients(): List<ClientRegistration> = clientRegistrationRepository.map { it }

    suspend fun allUsers(): List<User> {
        val users = userRepository.findAll()
            .collectList()
            .awaitSingleOrNull()
            ?: emptyList()
        log.debug("Users found {}.", users)
        return users
    }

    suspend fun getCachedUser(id: String) = userRepository
        .findById(id)
        .awaitSingleOrNull()

    suspend fun getUserCustomField(id: String): Long {
        throw UnsupportedOperationException()
    }

    private suspend fun saveUser(user: User): User {
        log.debug("Save user: {}", user)
        val rowsUpdated = dbClient.sql("insert into USERS (id, name, email, custom_field) values (:id, :name, :email, :custom_field)")
            .bind("id", user.id)
            .bind("name", user.name)
            .bind("email", user.email)
            .bind("custom_field", user.customField)
            .fetch()
            .awaitRowsUpdated()
        log.debug("'saveUser' rows updated: {}", rowsUpdated)
        return user
    }

    private suspend fun updateUser(user: User) {
        log.debug("Update user: {}", user)
        val rowsUpdated = dbClient.sql("UPDATE USERS SET id = :id, name = :name, email = :email, custom_field = :customField")
            .bindProperties(user)
            .fetch()
            .awaitRowsUpdated()
        log.debug("'updateUser' rows updated: {}.", rowsUpdated)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
