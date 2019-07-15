package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.VPSItem;
import com.audit.materialaudit.model.VPSItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VPSItemMapper {
    int countByExample(VPSItemExample example);

    int deleteByExample(VPSItemExample example);

    int insert(VPSItem record);

    int insertSelective(VPSItem record);

    List<VPSItem> selectByExample(VPSItemExample example);

    int updateByExampleSelective(@Param("record") VPSItem record, @Param("example") VPSItemExample example);

    int updateByExample(@Param("record") VPSItem record, @Param("example") VPSItemExample example);
}