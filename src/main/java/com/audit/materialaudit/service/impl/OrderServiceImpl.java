package com.audit.materialaudit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.audit.materialaudit.AlipayConfig;
import com.audit.materialaudit.common.annotation.CheckSign;
import com.audit.materialaudit.mapper.OrderCertMapper;
import com.audit.materialaudit.mapper.OrderInfoMapper;
import com.audit.materialaudit.mapper.UserMapper;
import com.audit.materialaudit.model.*;
import com.audit.materialaudit.model.request.HttpOrderRequest;
import com.audit.materialaudit.model.request.OrderRecordRequest;
import com.audit.materialaudit.model.request.PayRequest;
import com.audit.materialaudit.model.request.QueryOrderCertRequest;
import com.audit.materialaudit.model.response.BaseResponse;
import com.audit.materialaudit.model.response.CommonResponse;
import com.audit.materialaudit.model.response.NotifyResponse;
import com.audit.materialaudit.model.response.OrderRecordResponse;
import com.audit.materialaudit.service.OrderService;
import com.audit.materialaudit.utils.AlipayUtils;
import com.audit.materialaudit.utils.NumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Mapper
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderCertMapper orderCertMapper;

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    @Override
    @Transactional
    @CheckSign
    public String createOrder(HttpOrderRequest request){
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(request.getUserName()).andStatusEqualTo(1);
        List<User> userList = userMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(userList)){
            log.error("用户不存在");
            return null;
        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCount(request.getCount());
        orderInfo.setIsDelete((byte) 0);
        orderInfo.setStatus((byte) 0);
        orderInfo.setItemname(StringUtils.isEmpty(request.getItemName())? "vps":request.getItemName());
        Long orderId = Long.valueOf(System.currentTimeMillis()+""+NumUtils.genereteRandomNum(3));
        orderInfo.setOrderId(orderId);
        orderInfo.setUsername(request.getUserName());
        orderInfo.setUserid(userList.get(0).getId());
        orderInfo.setCreateTime(String.valueOf(System.currentTimeMillis()));
        orderInfo.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        orderInfo.setPayMoney(request.getPayMoney());
        try {
            orderInfoMapper.insert(orderInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        PayRequest.PayRequestBuilder payRequest = PayRequest.builder();
        payRequest.out_trade_no(orderId.toString()).subject("vps").total_amount(request.getPayMoney().toString()).body("vps");

        return  AlipayUtils.pay(payRequest.build());
    }

    @Override
    @Transactional
    public NotifyResponse notify(Map<String, String> request) throws Exception {
        NotifyResponse response = new NotifyResponse();
        OrderInfoExample example = new OrderInfoExample();
        if(StringUtils.isEmpty(request.get("out_trade_no"))){
            log.error("订单异常,订单号："+request.get("out_trade_no"));
            throw new Exception("订单号异常");
        }
        example.createCriteria().andOrderIdEqualTo(Long.valueOf(request.get("out_trade_no")));
        log.error("orderId==="+request.get("out_trade_no"));
        List<OrderInfo> orderInfos = orderInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(orderInfos) || orderInfos.get(0).getStatus() != 0 && orderInfos.get(0).getStatus() != 1){
            log.error("订单异常，订单数据："+orderInfos.get(0).toString());
            throw new Exception("订单异常");
        }
        Double money = Double.parseDouble(request.get("total_amount"));
        Integer payMoney = money.intValue();
        if(StringUtils.isEmpty(request.get("total_amount")) ||
                !orderInfos.get(0).getPayMoney().equals(payMoney)){
            log.error("金额校验失败，金额："+request.get("total_amount"));
            throw new Exception("金额校验失败");
        }
        OrderInfo info = new OrderInfo();
        info.setStatus((byte) 1);
        info.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        orderInfoMapper.updateByExampleSelective(info, example);

        OrderCert orderCert = new OrderCert();
        String orderCertString = RandomStringUtils.randomAlphanumeric(10);
        OrderCertExample orderCertExample = new OrderCertExample();
        orderCertExample.createCriteria().andCertNumEqualTo(orderCertString);
        List<OrderCert> orderCerts = orderCertMapper.selectByExample(orderCertExample);

        if(CollectionUtils.isEmpty(orderCerts)) {
            log.error("333333333");
            orderCert.setCertNum(orderCertString);
            orderCert.setOrderId(Long.valueOf(request.get("out_trade_no")));
            orderCert.setStatus((byte)0);
            orderCert.setUserid(orderInfos.get(0).getUserid());
            orderCert.setUsername(orderInfos.get(0).getUsername());
            orderCert.setCreateTime(String.valueOf(System.currentTimeMillis()));
            orderCert.setUpdateTime(String.valueOf(System.currentTimeMillis()));

            orderCertMapper.insert(orderCert);
        }else{
            response.setOrderId(request.get("out_trade_no"));
            response.setStatus(1);
            return response;
        }
        response.setOrderCert(orderCertString);
        log.error("444444444");
        OrderInfo info1 = new OrderInfo();
        info1.setStatus((byte) 2);
        info1.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        orderInfoMapper.updateByExampleSelective(info1, example);

        response.setOrderId(request.get("out_trade_no"));
        response.setStatus(2);
        return response;
    }

    @Override
    @Transactional
    public CommonResponse<List<OrderRecordResponse>> queryOrderRecord(OrderRecordRequest request) {
        final CountDownLatch latch = new CountDownLatch(2);
        List<OrderRecordResponse> orderRecordResponseList = new ArrayList<>();
        CommonResponse<List<OrderRecordResponse>> commonResponse = new CommonResponse<>();
        OrderInfoExample orderInfoExample = new OrderInfoExample();
        OrderInfoExample.Criteria orderCriteria = orderInfoExample.createCriteria();
        orderCriteria.andUsernameEqualTo(request.getUserName()).andStatusEqualTo((byte)2);
        if(!StringUtils.isEmpty(request.getOrderId())){
            orderCriteria.andOrderIdEqualTo(Long.valueOf(request.getOrderId()));
        }
        if(!StringUtils.isEmpty(request.getOrderStartTime() ) && !StringUtils.isEmpty(request.getOrderEndTime())){
            orderCriteria.andCreateTimeBetween(request.getOrderStartTime(),request.getOrderEndTime());
        }
        Future<Integer> future = threadPool.submit(() -> {
            try {
                return orderInfoMapper.countByExample(orderInfoExample);
            }finally {
                latch.countDown();
            }
        });
        List<OrderInfo> orderInfoList = new ArrayList<>();
        orderInfoExample.setLimit((request.getPageNum()-1)*request.getPageSize());
        orderInfoExample.setOffset((request.getPageNum()-1)*request.getPageSize()+request.getPageSize());
        threadPool.submit(() -> {
            try {
                orderInfoList.addAll(orderInfoMapper.selectByExample(orderInfoExample));
            }finally {
                latch.countDown();
            }
        });
        Integer count = null;
        try {
            latch.await(5, TimeUnit.SECONDS);
            count = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if(orderInfoList.isEmpty()){
            commonResponse.setData(new ArrayList<>());
            return commonResponse;
        }
        Map<Integer, OrderInfo> orderInfoMap = orderInfoList.stream().collect(Collectors.toMap(OrderInfo::getId, a -> a));
        commonResponse.setTotal(count);
        List<Long> orderIds = orderInfoList.stream().map(OrderInfo::getOrderId).collect(Collectors.toList());
        OrderCertExample certExample = new OrderCertExample();
        certExample.createCriteria().andOrderIdIn(orderIds);
        List<OrderCert> orderCertList = orderCertMapper.selectByExample(certExample);
        Map<Long, OrderCert> orderCertMap = orderCertList.stream().collect(Collectors.toMap(OrderCert::getOrderId,e->e));
        for(Integer orderId:orderInfoMap.keySet()){
            OrderRecordResponse orderRecordResponse = new OrderRecordResponse();
            orderRecordResponse.setCount(orderInfoMap.get(orderId).getCount());
            orderRecordResponse.setItemName(orderInfoMap.get(orderId).getItemname());
            orderRecordResponse.setOrderId(orderInfoMap.get(orderId).getOrderId().toString());
            orderRecordResponse.setPayMoney(orderInfoMap.get(orderId).getPayMoney());
            orderRecordResponse.setPayTime(Long.valueOf(orderInfoMap.get(orderId).getCreateTime()));
            if (orderCertMap.get(Long.valueOf(orderId)) != null){
                orderRecordResponse.setOrderCert(orderCertMap.get(Long.valueOf(orderId)).getCertNum());
            }
            log.info("orderRecordResponse:"+ JSON.toJSONString(orderRecordResponse));
            orderRecordResponseList.add(orderRecordResponse);

        }
        log.error("orderRecordResponseList:"+JSON.toJSONString(orderRecordResponseList));
        commonResponse.setData(orderRecordResponseList);
        return commonResponse;
    }

    @Override
    public String getOrderCert(QueryOrderCertRequest request) {
        OrderCertExample example = new OrderCertExample();
        String result = "";
        example.createCriteria().andUsernameEqualTo(request.getUserName()).andOrderIdEqualTo(Long.valueOf(request.getOrderId()));
        List<OrderCert> orderCertList = orderCertMapper.selectByExample(example);
        if(!orderCertList.isEmpty()){
            result = orderCertList.get(0).getCertNum();
        }
        return result;
    }
}
