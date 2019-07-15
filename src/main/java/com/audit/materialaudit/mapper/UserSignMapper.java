package com.audit.materialaudit.mapper;

import com.audit.materialaudit.model.UserSign;
import com.audit.materialaudit.model.UserSignExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserSignMapper {
    int countByExample(UserSignExample example);

    int deleteByExample(UserSignExample example);

    int insert(UserSign record);

    int insertSelective(UserSign record);

    List<UserSign> selectByExample(UserSignExample example);

    int updateByExampleSelective(@Param("record") UserSign record, @Param("example") UserSignExample example);

    int updateByExample(@Param("record") UserSign record, @Param("example") UserSignExample example);
}