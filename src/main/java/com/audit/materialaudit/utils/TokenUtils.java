package com.audit.materialaudit.utils;
import sun.misc.BASE64Decoder;

import java.io.IOException;

public class TokenUtils {

    public static String jiemi(String token){
        BASE64Decoder base64Decoder = new BASE64Decoder();

        try {
            byte[] a = base64Decoder.decodeBuffer(token);
            token = new String(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }
    public static String jiami(String token){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<token.length();i++){
            sb.append(String.valueOf((char) ((int)token.charAt(i)+token.length())));
        }
        return sb.toString();
    }
    public static void main(String[] args){
        String userName = "ningyunfa@163.com";
        System.out.println(jiami(userName));
        String token = jiami(userName);
        System.out.print(jiemi(token));
    }
}
