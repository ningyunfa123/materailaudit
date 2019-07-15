package com.audit.materialaudit.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CommonUtils {public CommonUtils() {
}

    public static String buildKey(Object req, Class<? extends Annotation> annotation) {
        Class<?> clazz = req.getClass();
        Field[] fields = clazz.getDeclaredFields();
        ArrayList params = new ArrayList();

        try {
            Field[] var5 = fields;
            int var6 = fields.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Field field = var5[var7];
                boolean fieldHasAnnotation = field.isAnnotationPresent(annotation);
                if (fieldHasAnnotation) {
                    Method method = clazz.getMethod("get" + toUpperFirstChar(field.getName()));
                    Object value = method.invoke(req);
                    if (value != null) {
                        params.add(value.toString());
                    }
                }
            }
        } catch (Exception var12) {
            throw new RuntimeException("build key exception : " + var12.getMessage());
        }

        return StringUtils.join(params, "_");
    }

    public static String toUpperFirstChar(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] = (char)(charArray[0] - 32);
        return String.valueOf(charArray);
    }

}
