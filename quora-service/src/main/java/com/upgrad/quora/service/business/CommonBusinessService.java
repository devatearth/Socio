package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

//This class is implemented as CommonBusinessService class
@Service
public class CommonBusinessService {

    //Creating an instance to access database
    @Autowired
    private UserDao userDao;

    //This method will validate the access token
    @Transactional
    public UserAuthEntity ValidateAccessToken(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthEntityByAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() == null) {
            return userAuthEntity;
        }
        int difference = userAuthEntity.getLogoutAt().compareTo(ZonedDateTime.now());
        if (difference < 0) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        return userAuthEntity;
    }

    //This method will get the details of the user
    @Transactional
    public UserEntity getUserByUserId(String userId) throws UserNotFoundException {
        UserEntity user = userDao.getUserByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        } else {
            return user;
        }
    }

}
