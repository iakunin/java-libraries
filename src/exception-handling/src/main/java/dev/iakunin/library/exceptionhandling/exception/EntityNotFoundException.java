package dev.iakunin.library.exceptionhandling.exception;

import dev.iakunin.library.exceptionhandling.AbstractThrowableProblem;
import java.util.UUID;
import org.zalando.problem.Status;

public class EntityNotFoundException extends AbstractThrowableProblem {

    public EntityNotFoundException(String entityName, UUID id) {
        super(
            Status.NOT_FOUND,
            String.format(
                "Unable to find '%s' entity by id='%s'",
                entityName,
                id
            )
        );
    }

}
