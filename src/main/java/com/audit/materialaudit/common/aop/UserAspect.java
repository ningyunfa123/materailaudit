package com.audit.materialaudit.common.aop;

import com.audit.materialaudit.model.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class UserAspect {

    @Pointcut(value = "execution(* com.audit.materialaudit.controller.*Controller.*(..))")
    public void init(){}

    @Around("init()")
    public void userCheck(){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null){
            Map<String, String[]> signMap = new HashMap<>();
            Map map = requestAttributes.getRequest().getParameterMap();
            signMap.putAll(map);
            String[] userNames = (String[]) map.get("userName");
            String userName = userNames[0];
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            HttpSession session = request.getSession();
            if (session != null){
                User currentUser = (User) session.getAttribute(userName);
                if (currentUser != null){
                    UserHolder.setUser(currentUser);
                }
            }
        }
    }
}
