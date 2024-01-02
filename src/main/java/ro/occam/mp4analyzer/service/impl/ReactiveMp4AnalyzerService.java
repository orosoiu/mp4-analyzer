package ro.occam.mp4analyzer.service.impl;

import ro.occam.mp4analyzer.dto.Atom;
import ro.occam.mp4analyzer.service.Mp4AnalyzerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import ro.occam.mp4analyzer.utils.HttpUtils;

import java.util.List;

/**
 * This implementation of {@link Mp4AnalyzerService} uses a reactive approach to read and process the file contents
 * The file is read using Netty's reactive HttpClient and then response is piped through a custom implementation
 * of a ByteToMessageDecoder that splits the response into frames, one for each atom, which are then returned as
 * a Flux of bytes (one item for each Atom's metadata + payload) to be processed individually
 */
@Component("mp4AnalyzerService")
@ConditionalOnProperty(
        value = "mp4.analyzer.read.strategy",
        havingValue = "reactive")
public class ReactiveMp4AnalyzerService extends Mp4AnalyzerService {

    @Autowired
    private HttpClient httpClient;

    @Override
    public List<Atom> analyze(String mp4Url) {

        return getContentsAsFlux(mp4Url)
                .map(this::extractAtom)
                .collectList()
                .block();
    }

    private Flux<byte[]> getContentsAsFlux(String mp4Url) {
        return httpClient.get()
                .uri(mp4Url)
                .response((httpClientResponse, byteBufFlux) -> {
                    int statusCode = httpClientResponse.status().code();
                    if (HttpUtils.isRedirectStatusCode(statusCode)) {
                        String newLocation = httpClientResponse.responseHeaders().get("Location");
                        if (StringUtils.hasText(newLocation)) {
                            return getContentsAsFlux(newLocation);
                        } else {
                            throw new RuntimeException("Location " + mp4Url + " has moved but no new location specified");
                        }
                    }
                    if (HttpUtils.isNotSuccessStatusCode(statusCode)) {
                        throw new RuntimeException("HTTP call to " + mp4Url + " returned unsupported status code " + statusCode);
                    }
                    return byteBufFlux.asByteArray();
                });
    }
}
