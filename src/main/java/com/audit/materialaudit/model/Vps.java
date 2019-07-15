package com.audit.materialaudit.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Vps {
    private Integer id;

    private Integer port;

    private String ip;

    private String password;

    private Byte useStatus;

    private Byte deleteFlag;

    private Long createTime;

    private Long updateTime;

    private Byte default1;

}