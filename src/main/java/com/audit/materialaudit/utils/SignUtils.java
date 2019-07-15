package com.audit.materialaudit.utils;

import com.audit.materialaudit.common.annotation.SignParam;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class SignUtils {


    public static final Integer SIGN_TYPE_MD5 = 1;
    public static final Integer SIGN_TYPE_SHA1 = 2;

    public SignUtils() {
    }

    public static Map<String, String> buildSignMap(Object object) {
        Map<String, String> map = new HashMap();
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        try {
            Field[] var4 = fields;
            int var5 = fields.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Field field = var4[var6];
                boolean fieldHasAnnotation = field.isAnnotationPresent(SignParam.class);
                if (fieldHasAnnotation) {
                    Method method = object.getClass().getMethod("get" + CommonUtils.toUpperFirstChar(field.getName()));
                    Object value = method.invoke(object);
                    if (value != null) {
                        map.put(field.getName(), String.valueOf(value));
                    }
                }
            }

            return map;
        } catch (Exception var11) {
            var11.printStackTrace();
            return Maps.newHashMap();
        }
    }

    public static String getMd5Sign(Map<String, String[]> map, String secret) {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);
        StringBuilder authInfo = new StringBuilder();
        boolean first = true;
        for (String key : keys) {
            if (map.get(key) != null) {
                String[] keyMap = map.get(key);
                // 参数值为空的不参与签名
                if (keyMap[0] != null && !keyMap[0].equals("")) {
                    if (first) {
                        first = false;
                    } else {
                        authInfo.append("&");
                    }
                    authInfo.append(key).append("=").append(keyMap[0]);
                }
            }
        }
        String md5Str = authInfo.toString();
        String md5StrKey = md5Str + "&key=" + secret;
        return DigestUtils.md5Hex(md5StrKey);
    }

    public static String getSha1Sign(Map<String, String> params, String secret) {
        String queryStr = getQueryStr(params, secret);
        return DigestUtils.shaHex(queryStr);
    }

    private static String getQueryStr(Map<String, String> params, String secret) {
        if (null != params && null != secret) {
            String[] keys = (String[])params.keySet().toArray(new String[0]);
            Arrays.sort(keys);
            List<String> paramsList = new ArrayList();
            String[] var4 = keys;
            int var5 = keys.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String key = var4[var6];
                String value = (String)params.get(key);
                if (value != null) {
                    paramsList.add(key + '=' + value);
                }
            }

            paramsList.add(secret);
            return StringUtils.join(paramsList, "&");
        } else {
            return "";
        }
    }

//    public static Boolean checkSign(Object signObject, String signStr, String signKey, Integer signType) {
//        if (null != signObject && null != signStr && null != signKey) {
//            String sign = buildSign(signObject, signType, signKey);
//            return sign.equals(signStr);
//        } else {
//            return false;
//        }
//    }

//    public static String buildSign(Object signObject, Integer signType, String signKey) {
//        signType = signType == null ? SIGN_TYPE_MD5 : signType;
//        Map<String, String> params = buildSignMap(signObject);
//        String sign = "";
//        if (SIGN_TYPE_MD5.equals(signType)) {
//            sign = getMd5Sign(params, signKey);
//        } else if (SIGN_TYPE_SHA1.equals(signType)) {
//            sign = getSha1Sign(params, signKey);
//        }
//
//        return sign;
//    }
}
