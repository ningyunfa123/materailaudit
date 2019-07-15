package com.audit.materialaudit.model.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderRecordResponse {

    private String orderId;
    private String itemName;
    private Integer payMoney;
    private Integer count;
    private Long payTime;
    private String orderCert;
}
