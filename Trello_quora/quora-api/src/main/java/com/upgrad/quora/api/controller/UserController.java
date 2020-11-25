package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class UserController {

    @RequestMapping(method = RequestMethod.POST,path = "/user/signup")
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest){
        return null;
    }
}
