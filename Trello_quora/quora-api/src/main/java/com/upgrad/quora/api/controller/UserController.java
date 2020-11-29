package com.upgrad.quora.api.controller;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

/* java imports */
import java.util.UUID;
import java.util.Base64;

/* app imports */
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;

/**
  * UserController.Class helps to serve as the start point for all user 
  * related functionalities - signup, signin and signout
  */

@RestController
@RequestMapping("/")
public class UserController {
  @Autowired
  private UserService userService;

  /* user signup handler */
  @RequestMapping(method = RequestMethod.POST, path = "/user/signup", 
  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, 
  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> signUp(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
    /* 1. generate the required encrypted details for the user entity */
    String userName = signupUserRequest.getUserName();
    String email = signupUserRequest.getEmailAddress();
    String password = signupUserRequest.getPassword();
    String[] encrytedStuff = userService.performValidate(userName, email, password);
    
    /* 2. build the user entity object here */
    String salt = encrytedStuff[0];
    String encPassword = encrytedStuff[1];
    UserEntity newUser = this.buildUserEntity(signupUserRequest, salt, encPassword);

    /* 3. perform the required signup */
    UserEntity registeredEntity = userService.performSignUp(newUser);

    /* 4. build the required sign up response object */
    SignupUserResponse signupResponse = new SignupUserResponse();
    signupResponse.setId(registeredEntity.getUuid());
    signupResponse.setStatus("USER SUCCESSFULLY REGISTERED");

    /* 5. send the required response to the client side */
    return new ResponseEntity(signupResponse, HttpStatus.OK);
  }
  
  /* user signin handler */
  @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SigninResponse> signIn(@RequestHeader("authorization") String authorization) throws AuthenticationFailedException {
    /* 1. authorization details are sent through the headers. you'll need to get the username and password decoded */
    byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
    String decodedText = new String(decode);
    String[] decodedArray = decodedText.split(":");

    /* 2. call the respective signin method that will help sign in the user */
    String userName = decodedArray[0];
    String password = decodedArray[1];
    UserAuthEntity userAuthEntity = userService.performSignIn(userName, password);

    /* 3. finally send the required response to the client side for confirming the success login */
    SigninResponse userResponse = new SigninResponse();
    userResponse.setId(userAuthEntity.getUuid());
    userResponse.setMessage("SIGNED IN SUCCESSFULLY");
    HttpHeaders headers = new HttpHeaders();
    headers.add("access-token", userAuthEntity.getAccessToken());

    return new ResponseEntity<SigninResponse>(userResponse,headers, HttpStatus.OK);
  }
  
  /* user signout handler */
  @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignoutResponse> signOut(@RequestHeader("authorization") String authTokenString) throws SignOutRestrictedException {
    UserEntity userEntity = userService.performSignOut(authTokenString);
    SignoutResponse soResponse = new SignoutResponse();
    soResponse.setId(userEntity.getUuid());
    soResponse.setMessage("SIGNED OUT SUCCESSFULLY");
    return new ResponseEntity(soResponse, HttpStatus.OK);
  }
  
  /* helps build the required user entity object - portions of this entity is raw and un processed*/
  private UserEntity buildUserEntity(final SignupUserRequest signupUserRequest, String salt, String encPassword) {
    UserEntity newUser = new UserEntity();
    newUser.setFirstName(signupUserRequest.getFirstName());
    newUser.setLastName(signupUserRequest.getLastName());
    newUser.setUserName(signupUserRequest.getUserName());
    newUser.setUuid(UUID.randomUUID().toString());
    newUser.setEmail(signupUserRequest.getEmailAddress());
    newUser.setCountry(signupUserRequest.getCountry());
    newUser.setAboutMe(signupUserRequest.getAboutMe());
    newUser.setDob(signupUserRequest.getDob());
    newUser.setContactNumber(signupUserRequest.getContactNumber());
    newUser.setPassword(encPassword);
    newUser.setSalt(salt);
    newUser.setRole("nonadmin");

    /* finally return the new user that has been built */
    return newUser;
  }
}
