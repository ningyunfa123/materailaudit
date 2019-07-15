package com.audit.materialaudit.model.response;

import lombok.Builder;
import lombok.Data;

@Data
public class BaseResponse<E> {

    private String errno;
    private String errMsg;
    private Integer total;
    private E data;
}
