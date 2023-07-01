package dev.iakunin.library.tests.parametrized;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.springframework.util.ResourceUtils;

public class JsonFileSourceProvider
    implements AnnotationConsumer<JsonFileSource>, ArgumentsProvider {

    private final List<String> resources = new ArrayList<>();

    @Override
    public void accept(JsonFileSource jsonFileSource) {
        addResource(jsonFileSource.file());
        addResource(jsonFileSource.expectFile());
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(resources)
            .map(
                r -> r.stream()
                    .map(this::getJsonResource)
                    .toArray()
            )
            .map(Arguments::of);
    }

    private void addResource(String resource) {
        if (!resource.isEmpty()) {
            this.resources.add(resource);
        }
    }

    private String getJsonResource(String file) {
        try {
            return new String(
                Files.readAllBytes(
                    ResourceUtils.getFile(String.format("classpath:%s", file)).toPath()
                )
            );
        } catch (final IOException err) {
            return null;
        }
    }
}
