package com.example.ssodemo

import com.example.ssodemo.db.UserDetails
import com.example.ssodemo.db.UserRepository
import kotlinx.coroutines.reactor.awaitSingle
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

    suspend fun allUsers(): List<UserDetails> {
        val users = userRepository.findAll().collectList().awaitSingle()
        log.debug("Users found {}.", users)
        return users
    }

    suspend fun getCachedUser(user: UserDetails): UserDetails = userRepository
        .findById(user.id)
        .awaitSingleOrNull() ?: user

    suspend fun lastUpdate(user: UserDetails): Long {
        when (val byId = userRepository.findById(user.id).awaitSingleOrNull()) {
            null -> return saveUser(user).lastUpdate
            else -> {
                val lastLogin = byId.lastUpdate
                updateUser(byId.copy(lastUpdate = System.currentTimeMillis()))
                return lastLogin
            }
        }
    }

    private suspend fun saveUser(user: UserDetails): UserDetails {
        log.debug("Save user: {}", user)
        val rowsUpdated = dbClient.sql("insert into USER_DETAILS (id, name, email, last_update) values (:id, :name, :email, :last_update)")
            .bind("id", user.id)
            .bind("name", user.name)
            .bind("email", user.email)
            .bind("last_update", user.lastUpdate)
            .fetch()
            .awaitRowsUpdated()
        log.debug("'saveUser' rows updated: {}", rowsUpdated)
        return user
    }

    private suspend fun updateUser(user: UserDetails) {
        log.debug("Update user: {}", user)
        val rowsUpdated = dbClient.sql("UPDATE USER_DETAILS SET id = :id, name = :name, email = :email, last_update = :lastUpdate")
            .bindProperties(user)
            .fetch()
            .awaitRowsUpdated()
        log.debug("'updateUser' rows updated: {}.", rowsUpdated)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
