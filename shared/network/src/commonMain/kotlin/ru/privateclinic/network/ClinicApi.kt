package ru.privateclinic.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ClinicApi private constructor(baseUrl: String?, private val client: HttpClient) {
    constructor(baseUrl: String?) : this(baseUrl, HttpClient { configureClinicClient() })

    private val baseUrl by lazy {
        val url = URLBuilder().takeFrom(requireNotNull(baseUrl) { "Configure the clinic API base URL" })
        require(url.protocolOrNull in setOf(URLProtocol.HTTP, URLProtocol.HTTPS) && url.host.isNotEmpty()) {
            "The clinic API base URL must be an absolute HTTP or HTTPS URL"
        }
        require(url.user == null && url.password == null && url.parameters.isEmpty() &&
            !url.trailingQuery && url.fragment.isEmpty()
        ) {
            "The clinic API base URL must contain only a host, port and optional path"
        }
        url.build()
    }

    suspend fun health(): String = get("health").bodyAsText()

    suspend inline fun <reified T> getJson(vararg path: String): T = get(*path).body()

    @PublishedApi
    internal suspend fun get(vararg path: String): HttpResponse = client.get(
        URLBuilder(baseUrl).appendPathSegments(*path, encodeSlash = true).build(),
    )

    fun close() = client.close()

    companion object {
        internal fun <Config : HttpClientEngineConfig> withEngine(
            baseUrl: String,
            factory: HttpClientEngineFactory<Config>,
            configure: Config.() -> Unit,
        ): ClinicApi = ClinicApi(baseUrl, HttpClient(factory) {
            engine(configure)
            configureClinicClient()
        })
    }
}

private fun HttpClientConfig<*>.configureClinicClient() {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
