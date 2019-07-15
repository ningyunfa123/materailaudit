package com.audit.materialaudit.model.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommonResponse<T> {
    private Integer total;
    private T data;
}
