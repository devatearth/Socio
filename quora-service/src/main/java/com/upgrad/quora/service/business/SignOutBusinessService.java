package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/*
This class implements the user sign-out feature
 */
@Service
public class SignOutBusinessService {
    //Creating an instance to access DB
    @Autowired
    private UserDao userDao;

    //Creating an instance to access DB
    @Autowired
    private UserAuthDao userAuthDao;
    /*This method will signout the user
    @Parm - accessToken
     */

    @Transactional
    public UserAuthEntity validateAccessToken(String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthEntityByAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        } else {
            return userAuthEntity;
        }
    }

    @Transactional
    public void performSignOut(UserAuthEntity userAuthEntity) {
        userAuthDao.updateUserAuthEntity(userAuthEntity);
    }
}
