package dev.iakunin.library.kafka.keyvaluestorage.exception;

public class KafkaStorageException extends RuntimeException {

    public KafkaStorageException(String message) {
        super(message);
    }

    public KafkaStorageException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public KafkaStorageException(Throwable throwable) {
        super(throwable);
    }
}
