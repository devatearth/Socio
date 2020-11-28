package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

@Service
public class AuthenticationService {
  @Autowired
  private UserDao userDao;

  @Autowired
  private PasswordCryptographyProvider cryptographyProvider;

  @Transactional
  public UserAuthEntity userSignin(String userName, String passWord) throws AuthenticationFailedException {
    /* first check if the user name is registered or not */
    UserEntity user = userDao.getUserByUserName(userName);
    if (user == null) {
      throw new AuthenticationFailedException("ATH-001", "This username does not exist");
    }
    
    /* if you are here, it means that we have a registered user name with us in the database */
    String hashedPassword = cryptographyProvider.encrypt(passWord.toCharArray(), user.getSalt());
    if (user.getPassword().equals(hashedPassword)) {
      System.out.println(">_ performing sign in process...");

      final ZonedDateTime now = ZonedDateTime.now();
      final ZonedDateTime expiresAt = now.plusHours(8);
      JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(hashedPassword);

      /* build the user auth token object */
      UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
      userAuthTokenEntity.setUser(user);
      userAuthTokenEntity.setUuid(user.getUuid());
      userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
      userAuthTokenEntity.setLoginAt(now);
      userAuthTokenEntity.setExpiresAt(expiresAt);
      
      userDao.createAuthToken(userAuthTokenEntity);
      userDao.updateUser(user);
      return userAuthTokenEntity;
    }
    else{
      throw new AuthenticationFailedException("ATH-002","Password failed");
    }
  }
}
