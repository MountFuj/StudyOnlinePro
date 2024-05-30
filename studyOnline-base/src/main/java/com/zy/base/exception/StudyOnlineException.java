package com.zy.base.exception;

/**
 * 自定义异常类
 */
public class StudyOnlineException extends RuntimeException{

    private String errMessage;

    public StudyOnlineException(){
        super();
    }

    public StudyOnlineException(String errMessage){
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage(){
        return errMessage;
    }

    public static void cast(CommonError commonError){
        throw new StudyOnlineException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new StudyOnlineException(errMessage);
    }
}
