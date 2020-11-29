package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Base64;

@RestController
@RequestMapping("/")
public class SignInController {
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignin(@RequestHeader("authorization") String authorization) throws AuthenticationFailedException {
        System.out.println(">_ user signin being fired....");
        /* 1. authorization details are sent through the headers. you'll need to get the username and password decoded */
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        /* 2. call the respective signin method that will help sign in the user */
        String userName = decodedArray[0];
        String password = decodedArray[1];
        UserAuthEntity userAuthEntity = authenticationService.userSignin(userName, password);

        /* 3. finally send the required response to the client side for confirming the success login */
        SigninResponse userResponse = new SigninResponse();
        userResponse.setId(userAuthEntity.getUuid());
        userResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthEntity.getAccessToken());

        System.out.println(">_ sending response to the client side...");
        return new ResponseEntity<SigninResponse>(userResponse,headers, HttpStatus.OK);
    }
}
