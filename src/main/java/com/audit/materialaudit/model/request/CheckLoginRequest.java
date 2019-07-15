package com.audit.materialaudit.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckLoginRequest {
    @NotEmpty(message = "userName can not empty")
    private String userName;
}
