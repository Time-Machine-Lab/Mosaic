package io.github.tml.mosaic.config.mosaic;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author welsir
 * @description :
 * @date 2025/6/17
 */
@Configuration
@Getter
public class MosaicHotSwapConfig {

    @Value("${mosaic.chunk.bind.port:9999}")
    private int port;

}