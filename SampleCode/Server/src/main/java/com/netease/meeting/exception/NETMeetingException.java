package com.netease.meeting.exception;

/**
 * @author HJ
 * @date 2021/11/12
 **/
public class NETMeetingException extends RuntimeException{

    private String errorCode;

    public NETMeetingException(String message) {
        super(message);
    }

    public NETMeetingException(Throwable cause) {
        super(cause);
    }

    public NETMeetingException(String message, Throwable cause) {
        super(message, cause);
    }
    public NETMeetingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public NETMeetingException(String errorCode, String message) {
        super(message, null);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
