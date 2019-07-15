package com.audit.materialaudit.service;

import com.audit.materialaudit.model.User;
import com.audit.materialaudit.model.request.UserForm;
import com.audit.materialaudit.model.response.BaseResponse;

import java.util.List;

public interface UserService {
    List<User> login(UserForm userForm);
    Boolean createUser(UserForm userForm) throws Exception;
    Boolean checkUser(String userName);
}
