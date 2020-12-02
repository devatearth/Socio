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
public class UserBusinessService {


    //Creating an instance to encrypt the password
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

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

    /*This method will signout the user
    @Parm - accessToken
     */

    @Transactional
    public UserAuthEntity validateAccessToken(String accessToken) throws SignUpRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthEntityByAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new SignUpRestrictedException("SGR-001", "User is not Signed in");
        } else {
            return userAuthEntity;
        }
    }

    @Transactional
    public void performSignOut(UserAuthEntity userAuthEntity) {
        userDao.updateUserAuthEntity(userAuthEntity);
    }

    /*
    @parm userName,passWord
    @Return UserAuthEntity
    This method will take username and password as input and checks the user exist in DB and performs
    respective task.
     */
    @Transactional
    public UserAuthEntity PerformUserSignin(String userName, String passWord) throws AuthenticationFailedException {
        UserEntity user = userDao.getUserByUserName(userName);
        if (user == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        String hashedPassword = cryptographyProvider.encrypt(passWord, user.getSalt());
        if (user.getPassword().equals(hashedPassword)) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(hashedPassword);
            UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
            userAuthTokenEntity.setUser(user);
            userAuthTokenEntity.setUuid(user.getUuid());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
            userAuthTokenEntity.setLoginAt(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);

            userDao.createAuthToken(userAuthTokenEntity);
            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }


    }

}

