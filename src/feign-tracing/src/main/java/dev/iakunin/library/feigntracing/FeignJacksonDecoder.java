package dev.iakunin.library.feigntracing;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.jackson.JacksonDecoder;
import java.util.List;

public class FeignJacksonDecoder extends JacksonDecoder {

    public FeignJacksonDecoder() {
        super(
            List.of(
                new JavaTimeModule()
            )
        );
    }
}
