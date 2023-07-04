package dev.iakunin.library.logging.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

public final class HeadersBuilder {

    public String build(HttpServletRequest request) {
        return build(
            Collections.list(request.getHeaderNames())
                .stream()
                .distinct()
                .collect(
                    Collectors.toMap(
                        headerName -> headerName,
                        headerName -> Collections.list(request.getHeaders(headerName))
                    )
                )
        );
    }

    public String build(HttpServletResponse response) {
        return build(
            response.getHeaderNames()
                .stream()
                .distinct()
                .collect(
                    Collectors.toMap(
                        headerName -> headerName,
                        headerName -> new ArrayList<>(response.getHeaders(headerName))
                    )
                )
        );
    }

    public String build(Map<String, List<String>> headers) {
        return build(CollectionUtils.toMultiValueMap(headers));
    }

    public String build(MultiValueMap<String, String> headers) {
        return HttpHeaders.formatHeaders(headers);
    }

}
