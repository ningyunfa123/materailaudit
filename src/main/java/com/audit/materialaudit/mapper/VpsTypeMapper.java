package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.VpsType;
import com.audit.materialaudit.model.VpsTypeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VpsTypeMapper {
    int countByExample(VpsTypeExample example);

    int deleteByExample(VpsTypeExample example);

    int insert(VpsType record);

    int insertSelective(VpsType record);

    List<VpsType> selectByExample(VpsTypeExample example);

    int updateByExampleSelective(@Param("record") VpsType record, @Param("example") VpsTypeExample example);

    int updateByExample(@Param("record") VpsType record, @Param("example") VpsTypeExample example);
}