package com.lcj.mutichannel.model;

public class RespBean {

    private Integer code;
    private String message;
    private Object data;

    //请求成功执行，不需要返回数据返回该方法。
    public static RespBean ok(String msg){
        return new RespBean(200, msg, null);
    }
    //需要返回数据使用本方法。
    public static RespBean ok(String msg, Object data){
        return new RespBean(200, msg, data);
    }
    //请求成功执行，不需要返回数据返回该方法。
    public static RespBean serverError(String msg) {
        return new RespBean(500, msg, null);
    }
    //需要返回数据使用本方法。
    public static RespBean serverError(String msg, Object data) {
        return new RespBean(500, msg, data);
    }

    public static RespBean requestError(String msg){
        return new RespBean(400, msg, null);
    }

    public static RespBean requestError(String msg, Object data){
        return new RespBean(400, msg, data);
    }

    private RespBean() {
    }

    public RespBean(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public RespBean setData(Object data) {
        this.data = data;
        return this;
    }
}



