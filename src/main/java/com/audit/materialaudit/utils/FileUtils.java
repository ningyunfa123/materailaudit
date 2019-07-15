package com.audit.materialaudit.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class FileUtils {
    public static String getFileContents(String filePath) throws IOException {
        BufferedReader fileBuf = null;
        StringBuilder sb = new StringBuilder();
        String contents = null;
        try{
            fileBuf = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
            while((contents = fileBuf.readLine())!=null){
                contents=contents.trim();
                sb.append(contents);
            }
        }catch (IOException e){
            e.printStackTrace();
            throw e;
        }
        return sb.toString();
    }
}
