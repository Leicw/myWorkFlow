package com.lcw.util;

/**
 * @author ManGo
 */
public class GlobalConfig {

    public static final Boolean Test = false;

    public enum ResponseCode{
//        成功
        SUCCESS("0","success"),
//        失败
        FAILED("1","failed");

        private final String code;
        private final String msg;

        ResponseCode(String code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
