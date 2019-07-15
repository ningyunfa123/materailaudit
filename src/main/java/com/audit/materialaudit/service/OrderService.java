package com.audit.materialaudit.service;

import com.alipay.api.AlipayApiException;
import com.audit.materialaudit.model.request.HttpOrderRequest;
import com.audit.materialaudit.model.request.OrderRecordRequest;
import com.audit.materialaudit.model.request.QueryOrderCertRequest;
import com.audit.materialaudit.model.response.BaseResponse;
import com.audit.materialaudit.model.response.CommonResponse;
import com.audit.materialaudit.model.response.NotifyResponse;
import com.audit.materialaudit.model.response.OrderRecordResponse;

import java.util.List;
import java.util.Map;

public interface OrderService {
    String createOrder(HttpOrderRequest request) throws Exception;
    NotifyResponse notify(Map<String,String> request) throws Exception;
    CommonResponse<List<OrderRecordResponse>> queryOrderRecord(OrderRecordRequest request);
    String getOrderCert(QueryOrderCertRequest request);
}
