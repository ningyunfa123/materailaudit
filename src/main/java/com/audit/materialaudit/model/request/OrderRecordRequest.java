package com.audit.materialaudit.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderRecordRequest {
    @NotEmpty
    private String userName;
    private String orderId;
    private String orderStartTime;
    private String orderEndTime;
    private Integer pageSize;
    private Integer pageNum;
}
