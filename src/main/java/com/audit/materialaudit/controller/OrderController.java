package com.audit.materialaudit.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.audit.materialaudit.AlipayConfig;
import com.audit.materialaudit.constant.ErrorContents;
import com.audit.materialaudit.mapper.UserMapper;
import com.audit.materialaudit.model.User;
import com.audit.materialaudit.model.request.HttpOrderRequest;
import com.audit.materialaudit.model.request.OrderNotifyRequest;
import com.audit.materialaudit.model.request.OrderRecordRequest;
import com.audit.materialaudit.model.request.QueryOrderCertRequest;
import com.audit.materialaudit.model.response.BaseResponse;
import com.audit.materialaudit.model.response.CommonResponse;
import com.audit.materialaudit.model.response.NotifyResponse;
import com.audit.materialaudit.model.response.OrderRecordResponse;
import com.audit.materialaudit.service.OrderService;
import com.audit.materialaudit.service.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Controller
@RequestMapping("vps/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/create")
    @ResponseBody
    public BaseResponse<String> createOrder(@Valid HttpOrderRequest request, HttpSession session){
        BaseResponse<String> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        String result = null;
        User user = (User) session.getAttribute(request.getUserName());
        if(user == null){
            response.setErrno(ErrorContents.USER_NOT_LOGIN);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.USER_NOT_LOGIN));
            return response;
        }
        try {
            result = orderService.createOrder(request);
        }catch (Exception e){
            response.setErrMsg(e.getMessage());
            e.printStackTrace();
        }
        if(StringUtils.isEmpty(result)){
            response.setErrno("1001");
            response.setErrMsg("faild");
            return response;
        }
        response.setData(result);
        return response;
    }
    @RequestMapping("/return")
    @ResponseBody
    public BaseResponse<NotifyResponse> payReturn(@Valid OrderNotifyRequest request){
        BaseResponse<NotifyResponse> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        Map<String,String> params = new HashMap<String,String>();
        params.put("out_trade_no",request.getOut_trade_no());
        params.put("total_amount",request.getTotal_amount());
        NotifyResponse notifyResponse = null ;
         try {
             notifyResponse = orderService.notify(params);
         }catch (Exception e){
             e.printStackTrace();
             response.setErrMsg(e.getMessage());
         }
         if(notifyResponse == null){
             response.setErrno("-1");
             response.setErrMsg("faild");
             return response;
         }else {
             response.setData(notifyResponse);
             return  response;
         }

    }

    @PostMapping("/notify")
    @ResponseBody
    public String notify(HttpServletRequest request){
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }
        boolean signVerified  = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE); //调用SDK验证签名
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        NotifyResponse notifyResponse = null ;
        if(signVerified){
            try {
                notifyResponse = orderService.notify(params);
            }catch (Exception e){
                return "failure";
            }
            if(notifyResponse == null){
                return "failure";
            }else {
                if(notifyResponse.getStatus() == 1 || notifyResponse.getStatus() == 2){
                    return "success";
                }else {
                    return "failure";
                }
            }
        }else {
            return "failure";
        }
    }

    @ResponseBody
    @RequestMapping("/orderRecord")
    public BaseResponse<CommonResponse<List<OrderRecordResponse>>> queryOrderRecord(@Valid OrderRecordRequest request, HttpSession session){
        BaseResponse<CommonResponse<List<OrderRecordResponse>>> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        User user = (User) session.getAttribute(request.getUserName());
        if(user == null){
            response.setErrno(ErrorContents.USER_NOT_LOGIN);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.USER_NOT_LOGIN));
            return response;
        }
        CommonResponse<List<OrderRecordResponse>> commonResponse = new CommonResponse<>();
        try{
            commonResponse = orderService.queryOrderRecord(request);
        }catch (Exception e){
            e.printStackTrace();
            response.setErrno(ErrorContents.SERVICE_ERROR);
            response.setErrMsg(ErrorContents.ERRORMAPPPING.get(ErrorContents.SERVICE_ERROR));
            return response;
        }
        response.setData(commonResponse);
        return response;
    }

    @ResponseBody
    @RequestMapping("/getOrderCert")
    public BaseResponse<String> getOrderCert(@Valid QueryOrderCertRequest request){
        BaseResponse<String> response = new BaseResponse<>();
        response.setErrno("0");
        response.setErrMsg("success");
        String result = "";
        try {
            result = orderService.getOrderCert(request);
        }catch (Exception e){
            e.printStackTrace();
            response.setErrno("-1");
            response.setErrMsg("faild");
        }
        response.setData(result);
        return response;
    }
}
