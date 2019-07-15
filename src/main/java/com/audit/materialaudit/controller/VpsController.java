package com.audit.materialaudit.controller;


import com.audit.materialaudit.constant.ErrorContents;
import com.audit.materialaudit.model.User;
import com.audit.materialaudit.model.Vps;
import com.audit.materialaudit.model.request.ApplyVPSRequest;
import com.audit.materialaudit.model.response.ApplyVPSResponse;
import com.audit.materialaudit.model.response.BaseResponse;
import com.audit.materialaudit.service.VPSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/vps")
@Slf4j
public class VpsController {

    @Autowired
    private VPSService vpsService;
    @ResponseBody
    @PostMapping("/apply")
    public BaseResponse<Vps> applyVps(ApplyVPSRequest applyVPSRequest, HttpServletRequest request){
        BaseResponse<Vps> response = new BaseResponse<>();
        ApplyVPSResponse vpsResponse = new ApplyVPSResponse();
        response.setErrno("0");
        response.setErrMsg("success");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if(user == null){
            response.setErrno(ErrorContents.USER_NOT_LOGIN);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.USER_NOT_LOGIN));
            return response;
        }
        applyVPSRequest.setUserId(user.getId());
        try {
            vpsResponse = vpsService.applyVPS(applyVPSRequest);
        }catch (Exception e){
            response.setErrno(ErrorContents.SERVICE_ERROR);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.SERVICE_ERROR));
            return response;
        }
        Vps vps = new Vps();
        BeanUtils.copyProperties(vpsResponse,vps);
        response.setData(vps);
        return response;
    }

}
