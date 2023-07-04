package dev.iakunin.library.exceptionhandling;

import dev.iakunin.library.logging.common.service.MdcFingerprintService;
import java.util.ServiceConfigurationError;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@ControllerAdvice
@RequiredArgsConstructor
@Import(ServiceConfigurationError.class)
public class ExceptionHandler implements ProblemHandling, SecurityAdviceTrait {

    private final MdcFingerprintService mdcFingerprintService;

    @Override
    public ResponseEntity<Problem> create(
        Throwable throwable,
        Problem problem,
        NativeWebRequest request,
        HttpHeaders headers
    ) {
        final ProblemBuilder problemBuilder = Problem.builder()
            .withType(problem.getType())
            .withTitle(problem.getTitle())
            .withStatus(problem.getStatus())
            .withDetail(problem.getDetail())
            .withInstance(problem.getInstance());
        problem.getParameters().forEach(problemBuilder::with);
        problemBuilder.with("sessionFingerprint", mdcFingerprintService.getSession());

        return ProblemHandling.super.create(
            throwable,
            problemBuilder.build(),
            request,
            headers
        );
    }
}
