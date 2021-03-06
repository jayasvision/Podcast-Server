package com.github.davinkevin.podcastserver.service

import com.github.davinkevin.podcastserver.entity.Cover
import com.github.davinkevin.podcastserver.service.image.CoverInformation
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.io.InputStream
import java.net.URI
import javax.imageio.ImageIO

/**
 * Created by kevin on 22/07/2018
 */
@Service
class ImageService(
        private val urlService: UrlService,
        wcbs: WebClient.Builder
) {

    private val log = LoggerFactory.getLogger(this.javaClass.name)!!
    private val wcb = wcbs
            .clientConnector(ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))

    fun getCoverFromURL(imageUrl: String?): Cover? {
        if (imageUrl == null || imageUrl.isEmpty())  {
            return null
        }

        return try {
            val image = urlService.asStream(imageUrl).use { it.toBufferedImage() }
                Cover().apply { url = imageUrl; height = image.height; width = image.width }
        } catch (e: Exception) {
            log.error("Error during fetching Cover information for {}", imageUrl)
            null
        }
    }

    fun fetchCoverInformation(imageUrl: String?): CoverInformation? {
        val c = getCoverFromURL(imageUrl) ?: return null
        return CoverInformation(c.width!!, c.height!!, URI(c.url!!))
    }

    fun fetchCoverInformation(url: URI): Mono<CoverInformation> {
        return wcb.baseUrl(url.toASCIIString()).build()
                .get()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(ByteArrayResource::class.java)
                .map { it.inputStream.toBufferedImage() }
                .map { CoverInformation(width = it.width, height = it.height, url = url) }
                .onErrorResume { Mono.empty() }
    }
}

private fun InputStream.toBufferedImage() = ImageIO.read(ImageIO.createImageInputStream(this))!!
