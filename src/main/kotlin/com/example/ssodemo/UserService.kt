package com.example.ssodemo

import com.example.ssodemo.db.User
import com.example.ssodemo.db.UserRepository
import com.example.ssodemo.model.UserDetails
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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

    suspend fun getUser(id: String) = userRepository
        .findById(id)
        .awaitSingleOrNull()

    suspend fun saveOrUpdateUser(userDetails: UserDetails): User {
        // todo: upsert sql command
        log.info("Save or update user: {}.", userDetails)
        val user = getUser(userDetails.id)

        val customField = "Custom field: ${LocalDateTime.now()}."
        return when (user) {
            null -> saveUser(User(userDetails.id, userDetails.name, customField))
            else -> updateUser(user.copy(customField = customField))
        }
    }

    private suspend fun saveUser(user: User): User {
        log.info("Save user: {}.", user)
        val rowsUpdated = dbClient.sql("insert into USERS (id, name, custom_field) values (:id, :name, :custom_field)")
            .bind("id", user.id)
            .bind("name", user.name)
            .bind("custom_field", user.customField)
            .fetch()
            .awaitRowsUpdated()
        log.debug("'saveUser' rows updated: {}.", rowsUpdated)
        return user
    }

    private suspend fun updateUser(user: User): User {
        log.info("Update user: {}.", user)
        val rowsUpdated = dbClient.sql("UPDATE USERS SET id = :id, name = :name, custom_field = :customField")
            .bindProperties(user)
            .fetch()
            .awaitRowsUpdated()
        log.debug("'updateUser' rows updated: {}.", rowsUpdated)
        return user
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
