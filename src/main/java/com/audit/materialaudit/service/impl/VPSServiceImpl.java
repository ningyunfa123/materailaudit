package com.audit.materialaudit.service.impl;

import com.audit.materialaudit.constant.constConfig;
import com.audit.materialaudit.mapper.*;
import com.audit.materialaudit.model.*;
import com.audit.materialaudit.model.request.ApplyVPSRequest;
import com.audit.materialaudit.model.response.ApplyVPSResponse;
import com.audit.materialaudit.service.VPSService;
import com.audit.materialaudit.utils.ShellUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class VPSServiceImpl implements VPSService {

    @Autowired
    private OrderCertMapper orderCertMapper;
    @Autowired
    private VpsMapper vpsMapper;
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private VpsRecordMapper vpsRecordMapper;
    @Override
    @Transactional
    public ApplyVPSResponse applyVPS(ApplyVPSRequest request) throws Exception {
        ApplyVPSResponse response = new ApplyVPSResponse();
        OrderCertExample example = new OrderCertExample();
        example.createCriteria().andUseridEqualTo(request.getUserId()).andCertNumEqualTo(request.getOrderCert()).andStatusEqualTo((byte)0);
        List<OrderCert> orderCertList = orderCertMapper.selectByExample(example);
        //校验兑换码状态是否正常
        if(CollectionUtils.isEmpty(orderCertList) || orderCertList.get(0).getStatus() != 0){
            throw  new Exception("兑换码异常");
        }

        //获取vps信息
        VpsExample vpsExample = new VpsExample();
        vpsExample.createCriteria().andUseStatusEqualTo((byte) 1).andDeleteFlagEqualTo((byte)0);
        List<Vps> vpsList = vpsMapper.selectByExample(vpsExample);
        if(vpsList.isEmpty()){
            log.error("vps数据异常，vps:"+vpsList.toString());
            throw new Exception("vps无库存");
        }
        //执行shell
        //ShellUtils.execShell(constConfig.shellPath,vpsList.get(0).getPort().toString());
        VpsRecord vpsRecord = new VpsRecord();
        OrderInfoExample orderInfoExample = new OrderInfoExample();
        orderInfoExample.createCriteria().andOrderIdEqualTo(orderCertList.get(0).getOrderId());
        List<OrderInfo> orderInfoList = orderInfoMapper.selectByExample(orderInfoExample);
        if(orderCertList.isEmpty()){
            throw new Exception("订单不存在");
        }
        vpsRecord.setUseTime(orderInfoList.get(0).getCount());
        vpsRecord.setVpsId(vpsList.get(0).getId());
        vpsRecord.setUserName(request.getUserName());
        vpsRecord.setUseAmount(300);
        vpsRecord.setType(Byte.valueOf(request.getVpsType()));
        vpsRecord.setPort(vpsList.get(0).getPort());
        vpsRecord.setIp(vpsList.get(0).getIp());
        vpsRecord.setCertCode(orderCertList.get(0).getCertNum());
        vpsRecord.setPassword(vpsList.get(0).getPassword());
        vpsRecord.setStatus((byte)1);
        vpsRecord.setDeleteFlag((byte)0);
        vpsRecord.setCreateTime(System.currentTimeMillis());
        vpsRecord.setUpdateTime(System.currentTimeMillis());
        vpsRecordMapper.insert(vpsRecord);
        Vps vps = new Vps();
        vps.setUseStatus((byte)2);
        vps.setUpdateTime(System.currentTimeMillis());
        VpsExample vpsExample1 = new VpsExample();
        vpsExample1.createCriteria().andIdEqualTo(vpsList.get(0).getId());
        vpsMapper.updateByExampleSelective(vps,vpsExample1);
        OrderCertExample orderCertExample = new OrderCertExample();
        orderCertExample.createCriteria().andIdEqualTo(orderCertList.get(0).getId());
        OrderCert orderCert = new OrderCert();
        orderCert.setStatus((byte)1);
        orderCert.setUpdateTime(System.currentTimeMillis()+"");
        orderCertMapper.updateByExampleSelective(orderCert,orderCertExample);
        response.setIp(vpsList.get(0).getIp());
        response.setPort(vpsList.get(0).getPort());
        response.setPassword(vpsList.get(0).getPassword());
        return response;
    }
}
