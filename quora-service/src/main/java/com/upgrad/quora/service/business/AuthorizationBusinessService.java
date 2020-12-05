package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuthorizationBusinessService {

    //Creating an instance to access UserAuthEntity from DB
    @Autowired
    private UserAuthDao userAuthDao;

    //This Method will fetch the UserAuthEntity From the DB by using the accessToken
    @Transactional
    public UserAuthEntity ValidateAccessToken(String  accessToken) throws AuthorizationFailedException {
        try {
            accessToken = accessToken.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            //Exception occurred
        }
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthEntityByAccessToken(accessToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }else{
            return userAuthEntity;
        }
    }
}
