package com.platform.frame.common.exception;


public class CommonException extends RuntimeException {

    public CommonException() {
    }

    /**
     * @param message
     */
    public CommonException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public CommonException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

}
