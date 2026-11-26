package ru.privateclinic.network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ClinicApiTest {
    @Serializable
    private data class Status(val available: Boolean)

    @Test
    fun readsJsonFromTheConfiguredPrefix() = runTest {
        for (baseUrl in listOf("https://clinic.test:8443/api/v1", "https://clinic.test:8443/api/v1/")) {
            val api = mockApi(baseUrl) {
                assertEquals(HttpMethod.Get, it.method)
                assertEquals("https://clinic.test:8443/api/v1/status", it.url.toString())
                assertEquals("application/json", it.headers[HttpHeaders.Accept])
                respond(
                    """{"available":true,"addedField":"ignored"}""",
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            }
            try {
                assertEquals(Status(available = true), api.getJson<Status>("status"))
            } finally {
                api.close()
            }
        }
    }

    @Test
    fun readsTheExistingTextHealthResponse() = runTest {
        val api = mockApi("https://clinic.test") {
            assertEquals("/health", it.url.encodedPath)
            respond("OK")
        }
        try {
            assertEquals("OK", api.health())
        } finally {
            api.close()
        }
    }

    @Test
    fun distinguishesHttpErrorsFromTransportFailures() = runTest {
        val serverUnavailable = mockApi("https://clinic.test") {
            respond("Unavailable", HttpStatusCode.ServiceUnavailable)
        }
        val offline = mockApi("https://clinic.test") { throw IOException("Offline") }
        try {
            assertEquals(
                HttpStatusCode.ServiceUnavailable,
                assertFailsWith<ServerResponseException> { serverUnavailable.health() }.response.status,
            )
            assertFailsWith<IOException> { offline.health() }
        } finally {
            serverUnavailable.close()
            offline.close()
        }
    }

    @Test
    fun rejectsRelativeAndCredentialedBaseUrls() = runTest {
        for (baseUrl in listOf("/api/", "//clinic.test/", "https://user:password@clinic.test/")) {
            val api = mockApi(baseUrl) { error("Unexpected request") }
            try {
                assertFailsWith<IllegalArgumentException> { api.health() }
            } finally {
                api.close()
            }
        }
    }

    private fun mockApi(baseUrl: String, handler: MockRequestHandler): ClinicApi =
        ClinicApi.withEngine(baseUrl, MockEngine) { addHandler(handler) }
}
