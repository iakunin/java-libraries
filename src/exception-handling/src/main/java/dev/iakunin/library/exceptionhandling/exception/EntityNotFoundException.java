package dev.iakunin.library.exceptionhandling.exception;

import dev.iakunin.library.exceptionhandling.AbstractThrowableProblem;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {

    public EntityNotFoundException(Class<?> entity, UUID id) {
        this(entity, id.toString());
    }

    public EntityNotFoundException(Class<?> entity, String id) {
        this(entity, "id", id);
    }

    public EntityNotFoundException(Class<?> entity, String fieldName, String fieldValue) {
        this(entity, new Field(fieldName, fieldValue));
    }

    public EntityNotFoundException(Class<?> entity, Field... fields) {
        super(
            Status.NOT_FOUND,
            String.format(
                "Unable to find entity '%s' by %s",
                entity.getName(),
                Stream.of(fields)
                    .map(
                        field -> String.format(
                            "'%s'='%s'",
                            field.getName(),
                            field.getValue()
                        )
                    )
                    .collect(Collectors.joining(" and "))
            )
        );
    }

    @Value
    public static class Field {
        String name;
        String value;
    }

}
