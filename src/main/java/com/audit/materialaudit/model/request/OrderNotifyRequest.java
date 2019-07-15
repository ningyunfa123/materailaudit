package com.audit.materialaudit.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class OrderNotifyRequest {
    @NotEmpty
    private String out_trade_no;
    @NotEmpty
    private String total_amount;
}
