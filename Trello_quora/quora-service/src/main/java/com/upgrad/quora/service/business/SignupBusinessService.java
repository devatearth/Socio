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

  @Autowired
  PasswordCryptographyProvider cryptor;

  @Transactional
  public UserEntity SignUp(UserEntity newUser) throws SignUpRestrictedException {
    System.out.println(">_ registering new user to the database...");
    return userDao.RegisterUser(newUser);
  }

  /* here we would be generating the salt and password enc only if the credentials are fresh */
  public String[] validate(String userName, String email, String password) 
  throws SignUpRestrictedException {
    System.out.println(">_ checking and generating encryption ...");
    /* if the username provided already exists in the current database */
    if(userDao.getUserByUserName(userName) != null){
      throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
    }
    /* if the email Id provided by the user already exists in the current database */
    else if(userDao.getUserByEmail(email) != null){
      throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
    }
    else {
      char[] userPassword = password.toCharArray();
      String[] encrytedStuff = cryptor.encrypt(userPassword);
      return encrytedStuff;
    }
  }
}

