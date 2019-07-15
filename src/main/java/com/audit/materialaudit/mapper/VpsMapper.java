package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.Vps;
import com.audit.materialaudit.model.VpsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VpsMapper {
    int countByExample(VpsExample example);

    int deleteByExample(VpsExample example);

    int insert(Vps record);

    int insertSelective(Vps record);

    List<Vps> selectByExample(VpsExample example);

    int updateByExampleSelective(@Param("record") Vps record, @Param("example") VpsExample example);

    int updateByExample(@Param("record") Vps record, @Param("example") VpsExample example);
}