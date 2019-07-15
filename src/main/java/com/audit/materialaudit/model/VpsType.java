package com.audit.materialaudit.model;

import lombok.Data;

@Data
public class VpsType {
    private Integer typeId;

    private Integer amount;

    private Byte status;

    private Byte deleteFlag;

    private Long createTime;

    private Long updateTime;

}