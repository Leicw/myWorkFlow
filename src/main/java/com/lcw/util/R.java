package com.lcw.util;

public class R<T> {

    private String code;
    private String msg;
    private T result;

    private R(String code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public static <T> R<T> success(T obj){
        R<T> r = new R<>(GlobalConfig.ResponseCode.SUCCESS.getCode(), GlobalConfig.ResponseCode.SUCCESS.getMsg(), obj);
        return r;
    }

    public static <T> R<T> failed(T obj){
        R<T> r = new R<>(GlobalConfig.ResponseCode.FAILED.getCode(), GlobalConfig.ResponseCode.FAILED.getMsg(), obj);
        return r;
    }

    public static <T> R<T> res(String code,String msg,T obj){
        R<T> r = new R<>(code, msg, obj);
        return r;
    }
}
