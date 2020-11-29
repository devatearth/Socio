package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class SignUpController {
  @Autowired
  private SignupBusinessService signupBusinessService;

  /*
  @Parm  = SignupUserRequest
  @return SignupUserResponse

  @Note- This method is mapped to /user/signup with an post method. This will register he user if everything is valid
   else This method will throw respective exception
   */
  @RequestMapping(method = RequestMethod.POST, path = "/user/signup", 
  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, 
  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
    System.out.println("Sign up request =======> " + signupUserRequest);

    /* 1. generate the required encrypted details for the user entity */
    String userName = signupUserRequest.getUserName();
    String email = signupUserRequest.getEmailAddress();
    String password = signupUserRequest.getPassword();
    String[] encrytedStuff = signupBusinessService.validate(userName, email, password);
    
    /* 2. build the user entity object here */
    String salt = encrytedStuff[0];
    String encPassword = encrytedStuff[1];
    System.out.println("Enc Password: " + encPassword);
    UserEntity newUser = this.buildUserEntity(signupUserRequest, salt, encPassword);

    /* 3. perform the required signup */
    UserEntity registeredEntity = signupBusinessService.SignUp(newUser);

    /* 4. build the required sign up response object */
    SignupUserResponse signupResponse = new SignupUserResponse();
    signupResponse.setId(registeredEntity.getUuid());
    signupResponse.setStatus("USER SUCCESSFULLY REGISTERED");

    /* 5. send the required response to the client side */
    System.out.println(">_ sending response to the client side...");
    return new ResponseEntity(signupResponse, HttpStatus.OK);
  }
  /* helps build the required user entity object - portions of this entity is raw and un processed*/
  private UserEntity buildUserEntity(final SignupUserRequest signupUserRequest, String salt, String encPassword) {
    System.out.println(">_ Creating new user entity...");
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

    /* finall return the new user that has been built */
    System.out.println(">_ User entity has been built");
    return newUser;
  }
}

