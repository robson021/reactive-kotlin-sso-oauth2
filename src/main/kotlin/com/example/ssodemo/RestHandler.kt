package com.example.ssodemo

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class RestHandler {
    suspend fun homePage(): ServerResponse = ServerResponse
        .ok()
        .contentType(MediaType.TEXT_PLAIN)
        .bodyValue("Hello world!")
        .awaitSingle()
}
