package dev.iakunin.library.feigntracing;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.jackson.JacksonEncoder;
import java.util.List;

public class FeignJacksonEncoder extends JacksonEncoder {

    public FeignJacksonEncoder() {
        super(
            List.of(
                new JavaTimeModule()
            )
        );
    }
}
