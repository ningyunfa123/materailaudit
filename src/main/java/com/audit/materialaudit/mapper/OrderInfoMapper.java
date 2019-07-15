package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.OrderInfo;
import com.audit.materialaudit.model.OrderInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderInfoMapper {
    int countByExample(OrderInfoExample example);

    int deleteByExample(OrderInfoExample example);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    List<OrderInfo> selectByExample(OrderInfoExample example);

    int updateByExampleSelective(@Param("record") OrderInfo record, @Param("example") OrderInfoExample example);

    int updateByExample(@Param("record") OrderInfo record, @Param("example") OrderInfoExample example);
}