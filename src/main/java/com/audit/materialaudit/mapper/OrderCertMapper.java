package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.OrderCert;
import com.audit.materialaudit.model.OrderCertExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderCertMapper {
    int countByExample(OrderCertExample example);

    int deleteByExample(OrderCertExample example);

    int insert(OrderCert record);

    int insertSelective(OrderCert record);

    List<OrderCert> selectByExample(OrderCertExample example);

    int updateByExampleSelective(@Param("record") OrderCert record, @Param("example") OrderCertExample example);

    int updateByExample(@Param("record") OrderCert record, @Param("example") OrderCertExample example);
}