package com.audit.materialaudit.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class HttpOrderRequest {
    @NotEmpty(message = "userName is empty")
    private String userName;
    @NotNull(message = "count is null")
    private Integer count;
    @NotNull(message = "payMoney is null")
    private Integer payMoney;
    @NotEmpty(message = "sign is empty")
    private String sign;
    private String itemName;
}
