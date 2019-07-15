package com.audit.materialaudit.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayRequest {
    private String out_trade_no;
    private String total_amount;
    private String subject;
    private String body;
}
