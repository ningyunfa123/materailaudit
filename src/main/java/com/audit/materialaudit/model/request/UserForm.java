package com.audit.materialaudit.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserForm {
    private String userName;
    @NotEmpty(message = "password is not exist")
    private String password;
    private String trueName;
}
