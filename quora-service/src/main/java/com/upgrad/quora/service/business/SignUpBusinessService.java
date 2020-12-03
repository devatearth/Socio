package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

/*
This class implements the user signup feature
 */
@Service
public class SignUpBusinessService {

    //Creating an instance to access DB
    @Autowired
    private UserDao userDao;

    /*
    This method will get the user as input and performs the task of creating the user.
    @Parm UserEntity
    @Return UserEntity
     */
    @Transactional
    public UserEntity performSignUp(UserEntity newUser) throws SignUpRestrictedException {
        if (userDao.getUserByUserName(newUser.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        } else if (userDao.getUserByEmail(newUser.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        } else {
            return userDao.RegisterUser(newUser);
        }

    }
}

