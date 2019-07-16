package com.audit.materialaudit.common.aop;

import com.audit.materialaudit.model.User;

public class UserHolder {
    private static final ThreadLocal<User> currentUserHolder = new ThreadLocal<>();

    public static User getUser(){
        return currentUserHolder.get();
    }
    public static void setUser(User user){
        currentUserHolder.set(user);
    }
    public static void remove(){
        currentUserHolder.remove();
    }

}
