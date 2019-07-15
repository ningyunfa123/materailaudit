package com.audit.materialaudit.utils;

import java.util.Arrays;

public class ShellUtils {
    public static Boolean execShell(String scriptPath, String... param) throws Exception {

        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            throw new Exception("操作系统非linux");
        }
        StringBuilder sb = new StringBuilder(scriptPath);
        if(param!=null &&param.length>0) {
            Arrays.stream(param).forEach(aPara -> sb.append(" ").append(aPara));
        }
        String[] cmd = new String[]{"/bin/sh","-c",sb.toString()};
        Runtime.getRuntime().exec(cmd,null,null);
        return true;
    }
}
