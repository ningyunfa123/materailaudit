package com.audit.materialaudit.service.impl;

import com.audit.materialaudit.mapper.UserMapper;
import com.audit.materialaudit.model.User;
import com.audit.materialaudit.model.UserExample;
import com.audit.materialaudit.model.request.UserForm;
import com.audit.materialaudit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> login(UserForm userForm) {
        UserExample example = new UserExample();
        UserExample.Criteria criteria =example.createCriteria();
        criteria.andPasswordEqualTo(userForm.getPassword()).andStatusEqualTo(1);
        if(!StringUtils.isEmpty(userForm.getUserName())){
            criteria.andUsernameEqualTo(userForm.getUserName());
        }else if(!StringUtils.isEmpty(userForm.getTrueName())){
            criteria.andTruenameEqualTo(userForm.getTrueName());
        }
        return userMapper.selectByExample(example);
    }

    @Override
    public Boolean createUser(UserForm userForm) throws Exception {
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        UserExample.Criteria criteriaor = example.createCriteria();
        criteria.andUsernameEqualTo(userForm.getUserName());
        criteriaor.andTruenameEqualTo(userForm.getTrueName());
        example.or(criteriaor);
        int userCount = userMapper.countByExample(example);
        if(userCount>0){
            throw new Exception("该用户名或邮箱已存在，请更换");
        }
        User user = new User();
        user.setUsername(userForm.getUserName());
        user.setTruename(userForm.getTrueName());
        user.setPassword(userForm.getPassword());
        user.setStatus(1);
        user.setCreatetime(new Date());
        userMapper.insert(user);
        return true;
    }

    @Override
    public Boolean checkUser(String userName) {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(userName);
        return userMapper.countByExample(example)<=0;
    }
}
