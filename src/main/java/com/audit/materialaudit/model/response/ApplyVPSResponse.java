package com.audit.materialaudit.model.response;

import lombok.Data;

@Data
public class ApplyVPSResponse {
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
