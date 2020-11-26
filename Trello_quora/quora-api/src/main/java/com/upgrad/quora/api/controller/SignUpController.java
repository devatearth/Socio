package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.PasswordCryptographyProvider;
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

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /*
    @Parm  = SignupUserRequest
    @return SignupUserResponse

    @Note- This method is mapped to /user/signup with an post method. This will register he user if everything is valid
     else This method will throw respective exception
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        //Transferring the SignupUserRequest to UserEntity
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
        newUser.setPassword(signupUserRequest.getPassword());
        newUser.setRole("nonadmin");
        //Encryption takes place here
        final String[] encrypt = cryptographyProvider.encrypt(signupUserRequest.getPassword());
        newUser.setSalt(encrypt[0]);
        newUser.setPassword(encrypt[1]);
        UserEntity createdUser = signupBusinessService.SignUp(newUser);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUser.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

}

