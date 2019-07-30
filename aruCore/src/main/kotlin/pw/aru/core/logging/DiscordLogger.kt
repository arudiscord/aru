package pw.aru.core.logging

import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.impl.EntityBuilder
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import pw.aru.utils.extensions.lang.sendAsync
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.util.concurrent.CompletableFuture
import java.net.http.HttpRequest.BodyPublishers.ofString as stringBody

open class DiscordLogger(val url: String) {
    companion object {
        private val client = HttpClient.newHttpClient()
        private val dummyBuilder = EntityBuilder(null)
    }

    var last: CompletableFuture<*> = CompletableFuture.completedFuture<Void>(null)

    fun embed(builder: EmbedBuilder.() -> Unit) = apply {
        last = last.thenCompose {
            client.sendAsync(discarding()) {
                uri(URI.create(url))
                header("Content-Type", "application/json")
                POST(
                    stringBody(
                        JsonObject().put(
                            "embeds",
                            JsonArray().add(dummyBuilder.embedToJson(EmbedBuilder().also(builder).build()))
                        ).encode()
                    )
                )
            }
        }
    }

    fun text(vararg value: String) = apply {
        last = last.thenCompose {
            client.sendAsync(discarding()) {
                uri(URI.create(url))
                header("Content-Type", "application/json")
                POST(
                    stringBody(
                        JsonObject().put("content", value.joinToString("\n")).encode()
                    )
                )
            }
        }
    }
}