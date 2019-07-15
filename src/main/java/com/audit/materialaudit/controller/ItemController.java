package com.audit.materialaudit.controller;

import com.audit.materialaudit.constant.ErrorContents;
import com.audit.materialaudit.model.User;
import com.audit.materialaudit.model.VPSItem;
import com.audit.materialaudit.model.request.QueryItemRequest;
import com.audit.materialaudit.model.response.BaseResponse;
import com.audit.materialaudit.service.ItemService;
import com.audit.materialaudit.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vps/item")
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;

    @ResponseBody
    @RequestMapping("/getItems")
    public BaseResponse<List<VPSItem>> queryItems(@CookieValue("TOKEN") String token, QueryItemRequest request, HttpSession session){
        BaseResponse<List<VPSItem>> response = new BaseResponse<>();

        String userName = TokenUtils.jiemi(token);
        log.info("userName===="+userName);
        User user = (User) session.getAttribute(request.getUserName());
        if(user == null){
            response.setErrno(ErrorContents.USER_NOT_LOGIN);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.USER_NOT_LOGIN));
            return response;
        }

        List<VPSItem> vpsItemList = new ArrayList<>();
        try{
            vpsItemList = itemService.queryItems();
        }catch (Exception e){
            e.printStackTrace();
            response.setErrno(ErrorContents.SERVICE_ERROR);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.SERVICE_ERROR));
            return response;
        }
        response.setData(vpsItemList);
        return response;
    }

}
