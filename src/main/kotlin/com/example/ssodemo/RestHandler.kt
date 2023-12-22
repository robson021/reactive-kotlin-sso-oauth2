package com.example.ssodemo

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse

@Component
class RestHandler {

    suspend fun homePage(): ServerResponse = simpleText("Hello world!")

    suspend fun testPage(): ServerResponse = simpleText("test page")

    private suspend inline fun simpleText(text: String) = ServerResponse
        .ok()
        .contentType(MediaType.TEXT_PLAIN)
        .bodyValue(text)
        .awaitSingle()
}
