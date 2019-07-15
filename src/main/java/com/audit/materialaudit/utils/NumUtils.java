package com.audit.materialaudit.utils;

import java.util.Random;

public class NumUtils {

    public static String genereteRandomNum(int Num) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Num; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
