package dev.iakunin.library.exceptionhandling.exception;

import dev.iakunin.library.exceptionhandling.AbstractThrowableProblem;
import java.util.UUID;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {

    public EntityNotFoundException(String entityName, UUID id) {
        this(entityName, id.toString());
    }

    public EntityNotFoundException(String entityName, String id) {
        this(entityName, "id", id);
    }

    public EntityNotFoundException(String entityName, String fieldName, String fieldValue) {
        super(
            Status.NOT_FOUND,
            String.format(
                "Unable to find '%s' entity by '%s'='%s'",
                entityName,
                fieldName,
                fieldValue
            )
        );
    }

}
