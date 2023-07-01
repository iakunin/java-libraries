package dev.iakunin.library.feigntracing;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CommonErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder innerDecoder;

    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("[Common ErrorDecoder] ####### fallback retry  #######");
        return innerDecoder.decode(methodKey, response);
    }
}
