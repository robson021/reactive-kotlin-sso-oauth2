package com.example.ssodemo

import com.example.ssodemo.db.User
import com.example.ssodemo.db.UserRepository
import com.example.ssodemo.extensions.getLogger
import com.example.ssodemo.model.UserDetails
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserService(
    private val clientRegistrationRepository: InMemoryReactiveClientRegistrationRepository,
    private val userRepository: UserRepository,
    private val dbClient: DatabaseClient,
) {
    suspend fun listOauthClients(): List<ClientRegistration> = clientRegistrationRepository.map { it }

    suspend fun allUsers(): List<User> {
        val users = userRepository.findAll()
            .collectList()
            .awaitSingleOrNull()
            ?: emptyList()
        log.debug("Users found {}", users)
        return users
    }

    suspend fun getUser(id: String) = userRepository
        .findById(id)
        .awaitSingleOrNull()

    suspend fun saveOrUpdateUser(userDetails: UserDetails): User {
        // todo: upsert sql command
        log.info("Save or update user: {}", userDetails)
        val user = getUser(userDetails.id)

        val customField = "Custom field: ${LocalDateTime.now()}."
        val country = LocaleContextHolder.getLocale().country
        return when (user) {
            null -> saveUser(User(userDetails.id, userDetails.name, country, customField))
            else -> updateUser(user.copy(country = country, customField = customField))
        }
    }

    private suspend fun saveUser(user: User): User {
        dbClient.sql("insert into USERS (id, name, country, custom_field) values (:id, :name, :country, :custom_field)")
            .bind("id", user.id)
            .bind("name", user.name)
            .bind("country", user.country)
            .bind("custom_field", user.customField)
            .fetch()
            .awaitRowsUpdated()
        log.info("Save: {}", user)
        return user
    }

    private suspend fun updateUser(user: User): User {
        dbClient.sql("UPDATE USERS SET name = :name, country = :country, custom_field = :customField WHERE id = :id")
            .bindProperties(user)
            .fetch()
            .awaitRowsUpdated()
        log.info("Update: {}", user)
        return user
    }

    companion object {
        private val log by getLogger()
    }
}
