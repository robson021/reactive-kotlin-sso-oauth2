package com.example.ssodemo

import com.example.ssodemo.model.UserFactory
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    private val testUser = UserFactory.fromGitHub(
        mapOf(
            "login" to "login",
            "name" to "name",
            "avatar_url" to "avatar_url",
        ),
        listOf("scope-1", "scope-2"),
    )

    @BeforeEach
    fun setup() {
        runBlocking { userService.saveOrUpdateUser(testUser) }
    }

    @Test
    fun `should get user by id`() {
        val user = runBlocking { userService.getUser(testUser.id)!! }
        assertThat(user).isNotNull
        assertThat(user.id).isEqualTo("login")
        assertThat(user.name).isEqualTo("name")
        assertThat(user.country).isNotNull()
        assertThat(user.customField).isNotNull()
    }

    @Test
    fun `should create new user`() {
        val newUser = testUser.copy(id = "new-id")
        val saved = runBlocking { userService.saveOrUpdateUser(newUser) }

        assertThat(saved.id).isEqualTo("new-id")
        assertThat(saved.name).isEqualTo("name")
        assertThat(saved.country).isNotNull()
        assertThat(saved.customField).isNotNull()
    }

    @Test
    fun `should create and then update user`() {
        val withNewName = testUser.copy(name = "new-name")
        val updated = runBlocking {
            userService.saveOrUpdateUser(withNewName)
        }
        assertThat(updated.name).isEqualTo("new-name")
        assertThat(updated.id).isEqualTo("login")
    }

    @Test
    fun `should get all users`() {
        val allUsers = runBlocking { userService.allUsers() }
        assertThat(allUsers).isNotEmpty
    }

    @Test
    fun `list oauth clients`() {
        val clients = runBlocking { userService.listOauthClients() }
        val ids = clients.map { it.registrationId }
        assertThat(ids).containsExactlyInAnyOrder("facebook", "google", "github")
    }

}