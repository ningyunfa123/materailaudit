package com.audit.materialaudit.controller;


import com.audit.materialaudit.constant.ErrorContents;
import com.audit.materialaudit.mapper.UserSignMapper;
import com.audit.materialaudit.model.User;
import com.audit.materialaudit.model.UserSign;
import com.audit.materialaudit.model.UserSignExample;
import com.audit.materialaudit.model.request.CheckLoginRequest;
import com.audit.materialaudit.model.request.SignRequest;
import com.audit.materialaudit.model.request.UserForm;
import com.audit.materialaudit.model.response.BaseResponse;
import com.audit.materialaudit.service.UserService;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vps/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserSignMapper userSignMapper;

    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<User> login(@Valid UserForm userForm, HttpSession session){
        log.error("sessionId==="+session.getId());
        BaseResponse<User> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        if(StringUtils.isEmpty(userForm.getUserName() )&& StringUtils.isEmpty(userForm.getTrueName())){
            response.setErrno("-1");
            response.setErrMsg("用户名为空");
            return response;
        }
        List<User> result = userService.login(userForm);
        if(!CollectionUtils.isEmpty(result)){
            log.error("user==="+result.get(0).getUsername());
            session.setAttribute(result.get(0).getUsername(),result.get(0));
            session.setMaxInactiveInterval(10*60);
            User user = result.get(0);
            user.setPassword("******");
            user.setNewpassword("******");
            response.setData(user);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/checklogin")
    public BaseResponse<Boolean> checkLogin(@Valid CheckLoginRequest checkLoginRequest , HttpSession session){
        log.error("username==="+checkLoginRequest.getUserName());
        log.error("sessionId==="+session.getId());
        String userName = checkLoginRequest.getUserName();
        User user = (User) session.getAttribute(userName);
        Boolean checkResult = false;
        if(user != null){
            checkResult = true;
        }
        BaseResponse<Boolean> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        response.setData(checkResult);

        return response;
    }

    @ResponseBody
    @PostMapping("createUser")
    public BaseResponse<Boolean> createUser(@Valid UserForm request){
        BaseResponse<Boolean> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        if(StringUtils.isEmpty(request.getTrueName()) || StringUtils.isEmpty(request.getUserName())){
            response.setErrno("1001");
            response.setErrMsg("用户名/邮箱为空");
            response.setData(false);
            return response;
        }
        Boolean result = false;
        try {
            result = userService.createUser(request);
        }catch (Exception e){
            response.setErrno("-1");
            response.setErrMsg(e.getMessage());
        }
        response.setData(result);
        return response;
    }

    @ResponseBody
    @RequestMapping("/checkUser")
    public BaseResponse<Boolean> checkUser(String userName){
        BaseResponse<Boolean> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        boolean result = userService.checkUser(userName);
        response.setData(result);
        return response;
    }

    /**
     * 保存更新sign验签密钥
     * @param signRequest
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/getList")
    public BaseResponse<List> getList(@Valid SignRequest signRequest, HttpServletRequest request){
        BaseResponse<List> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(signRequest.getUserName());
        if(user == null){
            response.setErrno(ErrorContents.USER_NOT_LOGIN);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.USER_NOT_LOGIN));
            return response;
        }
        try{
            UserSignExample example = new UserSignExample();
            example.createCriteria().andUserNameEqualTo(user.getUsername());
            List<UserSign> userSignList = userSignMapper.selectByExample(example);
            UserSign userSignOp = new UserSign();
            if(userSignList.isEmpty()){
                userSignOp.setUserName(user.getUsername());
                userSignOp.setUserSign(signRequest.getSign());
                userSignOp.setCreateTime(String.valueOf(System.currentTimeMillis()));
                userSignOp.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                userSignMapper.insert(userSignOp);
            }else {
                example.clear();
                example.createCriteria().andIdEqualTo(userSignList.get(0).getId());
                userSignOp.setUserSign(signRequest.getSign());
                userSignOp.setUpdateTime(String.valueOf(System.currentTimeMillis()));
                userSignMapper.updateByExampleSelective(userSignOp,example);
            }
        }catch (Exception e){
            response.setErrMsg(e.getMessage());
            return response;
        }
        response.setData(new ArrayList());
        return response;
    }


}
