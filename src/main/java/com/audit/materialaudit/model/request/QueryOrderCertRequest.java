package com.audit.materialaudit.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class QueryOrderCertRequest {
    @NotEmpty
    private String UserName;
    @NotEmpty
    private String orderId;
}
