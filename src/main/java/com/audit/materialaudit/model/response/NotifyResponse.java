package com.audit.materialaudit.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NotifyResponse {
    private Integer status;
    private String orderId;
    private String orderCert;
}
