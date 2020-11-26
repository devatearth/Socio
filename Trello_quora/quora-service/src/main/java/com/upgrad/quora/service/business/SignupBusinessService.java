package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional
    public UserEntity SignUp(UserEntity newUser) throws SignUpRestrictedException {
        if(userDao.getUserByUserName(newUser.getUserName()) != null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }else if(userDao.getUserByEmail(newUser.getEmail()) != null){
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }else{
           return userDao.RegisterUser(newUser);
        }

    }

}

