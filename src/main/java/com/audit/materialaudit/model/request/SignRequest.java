package com.audit.materialaudit.model.request;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class SignRequest {
    @NotEmpty(message = "userName is not Exist")
    private String userName;
    @NotEmpty(message = "sign is not exist")
    private String sign;
}
