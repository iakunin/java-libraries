package dev.iakunin.library.logging.reactive.wrapper;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import reactor.util.context.ContextView;

@RequiredArgsConstructor
public final class LoggerWrapper {

    private final ContextWrapper contextWrapper;

    public void logWithinMDC(Runnable logRunnable, ContextView context) {
        final Map<String, String> setToMdc = contextWrapper.getAll(context);

        final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        if (copyOfContextMap != null) {
            setToMdc.putAll(copyOfContextMap);
        }

        try {
            setToMdc.forEach(MDC::put);
            logRunnable.run();
        } finally {
            // Restoring original MDC state
            if (copyOfContextMap != null) {
                MDC.setContextMap(copyOfContextMap);
            }
        }
    }
}
