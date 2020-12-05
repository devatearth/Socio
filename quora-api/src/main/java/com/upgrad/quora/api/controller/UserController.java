package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.PasswordCryptographyProvider;
import com.upgrad.quora.service.business.SignInBusinessService;
import com.upgrad.quora.service.business.SignOutBusinessService;
import com.upgrad.quora.service.business.SignUpBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
public class UserController {

    //Creating an instance of SignupBusinessService to help with SignUp logic
    @Autowired
    private SignUpBusinessService signUpBusinessService;

    //Creating an instance of SignInBusinessService to help with signIn logic
    @Autowired
    private SignInBusinessService signInBusinessService;

    //Creating an instance of SignOutBusinessService to help with SignOut logic
    @Autowired
    private SignOutBusinessService signOutBusinessService;

    //Creating an instance of PasswordCryptographyProvider to help with encryption of password
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /*
    -> signup - "/user/signup"

    * This endpoint is used to register a new user in the Quora Application.
    * It should be a POST request
    * This endpoint requests for all the attributes in 'SignupUserRequest' about the user.
    * If the username provided already exists in the current database, throw ‘SignUpRestrictedException’ with the message code -'SGR-001' and message - 'Try any other Username, this Username has already been taken'.
    * If the email Id provided by the user already exists in the current database, throw ‘SignUpRestrictedException’ with the message code -'SGR-002' and message -'This user has already been registered, try with any other emailId'.
    * If the information is provided by a non-existing user, then save the user information in the database and return the 'uuid' of the registered user and message 'USER SUCCESSFULLY REGISTERED' in the JSON response with the corresponding
      HTTP status. Also, make sure to save the password after encrypting it using 'PasswordCryptographyProvider' class given in the stub file.
    * Also, when a user signs up using this endpoint then the role of the person will be 'nonadmin' by default.
      You can add users with 'admin' role only by executing database queries or with pgAdmin.
      * @Return ResponseEntity<SigninResponse>
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
        //Registering the user
        UserEntity createdUser = signUpBusinessService.performSignUp(newUser);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUser.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /*
    ->  signin - "/user/signin"
    * This endpoint is used for user authentication. The user authenticates in the application and after successful
      authentication, JWT token is given to a user.
    * It should be a POST request
    * This endpoint requests for the User credentials to be passed in the authorization field of header as part of
      Basic authentication. You need to pass "Basic username:password" (where username:password of the String is encoded
      to Base64 format) in the authorization header.
    * If the username provided by the user does not exist, throw "AuthenticationFailedException" with the message code
      -'ATH-001' and message-'This username does not exist'.
    * If the password provided by the user does not match the password in the existing database,
      throw 'AuthenticationFailedException' with the message code -'ATH-002' and message -'Password failed'.
    * If the credentials provided by the user match the details in the database, save the user login information in the
      database and return the 'uuid' of the authenticated user from 'users' table and message 'SIGNED IN SUCCESSFULLY' in
      the JSON response with the corresponding HTTP status. Note that 'JwtAccessToken' class has been given in the stub file
      to generate an access token.
    *Also, return the access token in the access_token field of the Response Header, which will be used by the user for
     any further operation in the Quora Application.
    @Return ResponseEntity<SigninResponse>
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> userSignin(@RequestHeader("authorization") String authorization) throws AuthenticationFailedException {
        //Formatting to get the username and password.
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        //The username will be in array index 0 and password will be in array index 1
        UserAuthEntity userAuthEntity = signInBusinessService.PerformUserSignIn(decodedArray[0], decodedArray[1]);
        //Building the response
        SigninResponse userResponse = new SigninResponse();
        userResponse.setId(userAuthEntity.getUuid());
        userResponse.setMessage("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(userResponse, headers, HttpStatus.OK);

    }

    /*
    -> signout - "/user/signout"
    * This endpoint is used to sign out from the Quora Application. The user cannot access any other endpoint once he is
      signed out of the application.
    * It should be a POST request.
    * This endpoint must request the access token of the signed in user in the authorization field of the Request Header.
    * If the access token provided by the user does not exist in the database, throw 'SignOutRestrictedException' with
      the message code -'SGR-001' and message - 'User is not Signed in'.
    * If the access token provided by the user is valid, update the LogoutAt time of the user in the database and
      return the 'uuid' of the signed out user from 'users' table and message 'SIGNED OUT SUCCESSFULLY' in the JSON
      response with the corresponding HTTP status.
     @Return SignoutResponse
     */

    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> userSignOut(@RequestHeader("authorization") String accesstoken) throws SignOutRestrictedException {
        //Signing out the user
        UserAuthEntity userAuthEntity = signOutBusinessService.validateAccessToken(accesstoken);
        //Updating the logout time
        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        signOutBusinessService.performSignOut(userAuthEntity);
        //Building the response
        SignoutResponse userResponse = new SignoutResponse();
        userResponse.setId(userAuthEntity.getUuid());
        userResponse.setMessage("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(userResponse, HttpStatus.OK);
    }
}


