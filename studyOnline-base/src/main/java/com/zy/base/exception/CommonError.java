package com.zy.base.exception;

public enum CommonError {
    UNKNOWN_ERROR("执行过程异常，请重试"),
    PARAM_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");

    private final String errMessage;

    public String getErrMessage(){
        return errMessage;
    }

    private CommonError(String errMessage){
        this.errMessage = errMessage;
    }
}
