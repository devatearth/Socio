package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

/*
This class implements the user sign-in feature
 */
@Service
public class SignInBusinessService {

    //Creating an instance to encrypt the password
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    //Creating an instance to access DB
    @Autowired
    private UserDao userDao;

    //Creating an instance to access DB
    @Autowired
    private UserAuthDao userAuthDao;
    /*

    @parm userName,passWord
    @Return UserAuthEntity
    This method will take username and password as input and checks the user exist in DB and performs
    respective task.
     */
    @Transactional
    public UserAuthEntity PerformUserSignIn(String userName, String passWord) throws AuthenticationFailedException {
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

            userAuthDao.createAuthToken(userAuthTokenEntity);
            return userAuthTokenEntity;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }


    }
}
