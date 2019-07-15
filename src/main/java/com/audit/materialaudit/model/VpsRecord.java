package com.audit.materialaudit.model;

import lombok.Data;

@Data
public class VpsRecord {
    private Integer id;

    private Integer port;

    private String ip;

    private String password;

    private String userName;

    private Byte status;

    private Integer useTime;

    private Byte type;

    private Byte deleteFlag;

    private Long createTime;

    private Long updateTime;

    private Integer vpsId;

    private Integer useAmount;

    private String certCode;

}