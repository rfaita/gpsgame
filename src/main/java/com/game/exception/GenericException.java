package com.game.exception;

import org.slf4j.MDC;

public class GenericException extends RuntimeException {

    private String traceId;

    public static GenericException of(String message, Object... attributes) {
        return new GenericException(String.format(message, attributes));
    }

    public GenericException() {
    }

    public GenericException(String message) {
        super(message);
        this.traceId = MDC.get("X-B3-TraceId");
    }

    public GenericException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenericException(Throwable cause) {
        super(cause);
    }

    public GenericException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getTraceId() {
        return traceId;
    }
}
