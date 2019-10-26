package com.yoryz.netty.util;

/**
 * TODO
 *
 * @author Yory
 * @version 1.0
 * @date 2019/5/16 21:18
 */
public class MyResponse {

    /**
     * response status
     */
    private Integer status;

    /**
     * response message
     */
    private String msg;

    /**
     * response data
     */
    private Object data;

    private static final Integer OK = 2000;
    private static final Integer OK_NO_DATA = 2001;

    private static final Integer ERROR = 4000;
    private static final Integer ERROR_NOT_FOUND = 4004;

    private static final Integer SERVER_EXCEPTION = 5000;

    private MyResponse(){}

    private MyResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public static MyResponse ok(Object data, String msg) {
        return new MyResponse(OK, msg, data);
    }

    public static MyResponse okNoData(String msg) {
        return new MyResponse(OK_NO_DATA, msg, null);
    }

    public static MyResponse errorMsg(String msg) {
        return new MyResponse(ERROR, msg, null);
    }

    public static MyResponse errorNotFound(String msg) {
        return new MyResponse(ERROR_NOT_FOUND, "'" + msg + "' not found!", null);
    }

    public static MyResponse serverException(String msg) {
        return new MyResponse(SERVER_EXCEPTION, msg, null);
    }

    public static boolean isOk(MyResponse myResponse) {
        return OK.equals(myResponse.getStatus());
    }

    public static boolean isNotFound(MyResponse myResponse) {
        return ERROR_NOT_FOUND.equals(myResponse.getStatus());
    }

    public static boolean isRequestError(MyResponse myResponse) {
        return ERROR.equals(myResponse.getStatus());
    }

    public static boolean isServerException(MyResponse myResponse) {
        return SERVER_EXCEPTION.equals(myResponse.getStatus());
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
