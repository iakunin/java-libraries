package dev.iakunin.library.logging.common.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

public final class HeadersBuilder {

    public String build(Map<String, List<String>> headers) {
        return build(CollectionUtils.toMultiValueMap(headers));
    }

    public String build(MultiValueMap<String, String> headers) {
        return formatHeaders(headers);
    }

    private String formatHeaders(MultiValueMap<String, String> headers) {
        return headers.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
            .flatMap(
                entry -> entry.getValue()
                    .stream()
                    .map(value -> String.format("%s: %s", entry.getKey(), value))
            ).collect(Collectors.joining("\n"));
    }

}
