package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.VpsRecord;
import com.audit.materialaudit.model.VpsRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface VpsRecordMapper {
    int countByExample(VpsRecordExample example);

    int deleteByExample(VpsRecordExample example);

    int insert(VpsRecord record);

    int insertSelective(VpsRecord record);

    List<VpsRecord> selectByExample(VpsRecordExample example);

    int updateByExampleSelective(@Param("record") VpsRecord record, @Param("example") VpsRecordExample example);

    int updateByExample(@Param("record") VpsRecord record, @Param("example") VpsRecordExample example);
}