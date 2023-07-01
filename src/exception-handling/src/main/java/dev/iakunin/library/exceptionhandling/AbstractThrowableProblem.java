package dev.iakunin.library.exceptionhandling;

import com.google.common.base.CaseFormat;
import java.net.URI;
import java.util.Map;
import org.zalando.problem.Status;

public abstract class AbstractThrowableProblem
    extends org.zalando.problem.AbstractThrowableProblem {

    public AbstractThrowableProblem(
        Status status,
        String detail
    ) {
        this(status, detail, null);
    }

    public AbstractThrowableProblem(
        Status status,
        String detail,
        Throwable cause
    ) {
        super(
            null,
            null,
            status,
            buildDetail(detail, cause)
        );
    }

    @Override
    public URI getType() {
        return URI.create(
            CaseFormat.UPPER_CAMEL.to(
                CaseFormat.LOWER_HYPHEN,
                withoutExceptionSuffix(this.getClass().getSimpleName())
            )
        );
    }

    @Override
    public String getTitle() {
        return withoutExceptionSuffix(this.getClass().getSimpleName());
    }

    @Override
    public Map<String, Object> getParameters() {
        return Map.of(
            "errorKey",
            String.format(
                "error.%s",
                CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_CAMEL,
                    withoutExceptionSuffix(this.getClass().getSimpleName())
                )
            )
        );
    }

    private String withoutExceptionSuffix(String className) {
        final String suffix = "Exception";
        if (className.endsWith(suffix)) {
            return className.substring(0, className.length() - suffix.length());
        }

        return className;
    }

    private static String buildDetail(String originalDetail, Throwable cause) {
        final var stringBuilder = new StringBuilder(originalDetail);

        if (cause != null) {
            stringBuilder.append(". ");
            stringBuilder.append(
                String.format(
                    "Exception was caused by '%s' (with message: '%s')",
                    cause.getClass().getName(),
                    cause.getMessage()
                )
            );
        }

        return stringBuilder.toString();
    }
}
