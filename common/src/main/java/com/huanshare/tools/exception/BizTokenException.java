package com.huanshare.tools.exception;

/**
 * 业务token异常
 *  2015/6/19.
 */
public class BizTokenException extends RuntimeException {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    public BizTokenException(String errorMessage){
        super(errorMessage);
    }

    public BizTokenException(String errorMessage, Throwable cause){
        super(errorMessage,cause);
    }

    public BizTokenException(String errorMessage, String errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BizTokenException(String errorMessage, String errorCode, Throwable cause){
        super(errorMessage,cause);
        this.errorCode = errorCode;
    }
}
