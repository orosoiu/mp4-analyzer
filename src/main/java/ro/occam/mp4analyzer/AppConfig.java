package ro.occam.mp4analyzer;

import ro.occam.mp4analyzer.service.decoder.AtomFrameDecoder;
import io.netty.handler.codec.http.HttpClientCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;

@Configuration
public class AppConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .doOnChannelInit((connectionObserver, channel, socketAddress) -> {
                    channel.pipeline().addBefore("reactor.left.httpCodec", "AtomFrameDecoder", new AtomFrameDecoder());
                    channel.pipeline().replace("reactor.left.httpCodec", "reactor.left.httpCodec", new HttpClientCodec(4096, 8192, Integer.MAX_VALUE, false));
                })
                .followRedirect(true)
                .keepAlive(false);
    }
}
