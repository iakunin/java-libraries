package dev.iakunin.library.logging.service;

import dev.iakunin.library.logging.configuration.Properties;
import org.slf4j.MDC;

public final class MdcFingerprintService {

    private final String sessionKey;
    private final String processKey;

    public MdcFingerprintService(Properties properties) {
        this.sessionKey = properties.getMdcKeys().getFingerprint().getSession();
        this.processKey = properties.getMdcKeys().getFingerprint().getProcess();
    }

    public String getSession() {
        return MDC.get(sessionKey);
    }

    public void set(String sessionFingerprint, String processFingerprint) {
        MDC.put(sessionKey, sessionFingerprint);
        MDC.put(processKey, processFingerprint);
    }

    public void unset() {
        MDC.remove(sessionKey);
        MDC.remove(processKey);
    }
}
