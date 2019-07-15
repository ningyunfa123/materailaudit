package com.audit.materialaudit.utils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.audit.materialaudit.AlipayConfig;
import com.audit.materialaudit.model.request.PayRequest;
import lombok.extern.slf4j.Slf4j;
import java.net.URLEncoder;

@Slf4j
public class AlipayUtils {

    public static String pay(PayRequest request) {


        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, "json", AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConfig.RETURN_URL);
        alipayRequest.setNotifyUrl(AlipayConfig.NOTIFY_URL);


        alipayRequest.setBizContent("{\"out_trade_no\":\""+ request.getOut_trade_no() +"\","
                + "\"total_amount\":\""+ request.getTotal_amount() +"\","
                + "\"subject\":\""+ request.getSubject() +"\","
                + "\"body\":\""+ request.getBody() +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");


        String result = null;
        String requestUrl = null;
        //请求
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
        //return result.substring(result.indexOf("action=")+8,result.indexOf("\"",result.indexOf("action=")+8))+"&biz_content="+URLEncoder.encode(alipayRequest.getBizContent());
    }
}
