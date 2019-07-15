package com.audit.materialaudit.constant;

import java.util.HashMap;
import java.util.Map;

public class ErrorContents {

    public static final String USER_NOT_LOGIN = "1001";
    public static final String SERVICE_ERROR = "1003";


    public static final Map<String,String>  ERRORMAPPPING = new HashMap<String, String>(){

        {
            put(USER_NOT_LOGIN,"用户未登录");
            put(SERVICE_ERROR,"服务异常");
        }
    };
}
