package dev.iakunin.library.logging.reactive.wrapper;

import dev.iakunin.library.logging.common.configuration.Properties;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

public final class ContextWrapper {

    private final String requestPathKey;
    private final String requestQueryStringKey;
    private final String requestIdKey;
    private final String sessionFingerprintKey;
    private final String processFingerprintKey;

    public ContextWrapper(Properties properties) {
        this.requestPathKey = properties.getMdcKeys().getRequest().getPath();
        this.requestQueryStringKey = properties.getMdcKeys().getRequest().getQueryString();
        this.requestIdKey = properties.getMdcKeys().getRequest().getId();
        this.sessionFingerprintKey = properties.getMdcKeys().getFingerprint().getSession();
        this.processFingerprintKey = properties.getMdcKeys().getFingerprint().getProcess();
    }

    public Map<String, String> getAll(ContextView context) {
        final Map<String, String> map = new ConcurrentHashMap<>();
        map.put(requestPathKey, getRequestPath(context));
        map.put(requestQueryStringKey, getRequestQueryString(context));
        map.put(requestIdKey, getRequestId(context));
        map.put(sessionFingerprintKey, getSessionFingerprint(context));
        map.put(processFingerprintKey, getProcessFingerprint(context));

        return map;
    }

    public Context putRequestPath(Context context, String requestPath) {
        return context.put(requestPathKey, requestPath);
    }

    public String getRequestPath(ContextView context) {
        return context.get(requestPathKey);
    }

    public Context putRequestQueryString(Context context, String requestQueryString) {
        return context.put(requestQueryStringKey, requestQueryString);
    }

    public String getRequestQueryString(ContextView context) {
        return context.get(requestQueryStringKey);
    }

    public Context putRequestId(Context context, String requestId) {
        return context.put(requestIdKey, requestId);
    }

    public String getRequestId(ContextView context) {
        return context.get(requestIdKey);
    }

    public Context putSessionFingerprint(Context context, String sessionFingerprint) {
        return context.put(sessionFingerprintKey, sessionFingerprint);
    }

    public String getSessionFingerprint(ContextView context) {
        return context.get(sessionFingerprintKey);
    }

    public Context putProcessFingerprint(Context context, String processFingerprint) {
        return context.put(processFingerprintKey, processFingerprint);
    }

    public String getProcessFingerprint(ContextView context) {
        return context.get(processFingerprintKey);
    }
}
