package com.audit.materialaudit.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class ApplyVPSRequest {

    @NotEmpty(message = "orderCert is not exist")
    private String orderCert;

    @NotEmpty(message = "vpsType is no exist")
    private String vpsType;

    @NotEmpty(message = "sign is not exist")
    private String sign;
    private String password;
    private Integer monthAmount;
    private String userName;
    private String ip;
    private Integer port;
    private Integer userId;
}
