package com.audit.materialaudit.common.aop;

import com.audit.materialaudit.mapper.UserSignMapper;
import com.audit.materialaudit.model.UserSign;
import com.audit.materialaudit.model.UserSignExample;
import com.audit.materialaudit.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Aspect
public class CheckSignAspect {

    @Autowired
    private UserSignMapper userSignMapper;
    @Pointcut("@annotation(com.audit.materialaudit.common.annotation.CheckSign)")
    public void checkSign(){}

    @Before("checkSign()")
    public void check(JoinPoint joinPoint) throws Exception {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null) {
            Map<String, String[]> signMap = new HashMap<>();
            Map map = requestAttributes.getRequest().getParameterMap();
            signMap.putAll(map);
            signMap.remove("sign");
            // 获取签名key
            String[] userNames = (String[]) map.get("userName");
            String userName = userNames[0];
            log.error("userNames=="+userNames+",userName=="+userName);
            UserSignExample example = new UserSignExample();
            example.createCriteria().andUserNameEqualTo(userName);
            List<UserSign> userSignList = userSignMapper.selectByExample(example);
            if(userSignList.isEmpty() || StringUtils.isEmpty(userSignList.get(0).getUserSign())){
                throw new Exception("密钥异常");
            }
            String secret = userSignList.get(0).getUserSign();
            // 生成签名
            String sign = SignUtils.getMd5Sign(signMap, secret);
            log.error("sign:"+sign);
            log.error("requestSign=="+requestAttributes.getRequest().getParameter("sign"));
            if (requestAttributes.getRequest().getParameter("sign") == null || !requestAttributes.getRequest()
                    .getParameter("sign").equals(sign)) {
                throw  new Exception("签名错误");
            }
        }
    }
}
