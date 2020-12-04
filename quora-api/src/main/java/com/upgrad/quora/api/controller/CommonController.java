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
import org.springframework.web.bind.annotation.PathVariable;

/* app imports */
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

/**
  * CommonController.Class will help handle all common operations in the 
  * app for all users
  */

@RestController
@RequestMapping("/")
public class CommonController {
  @Autowired
  private UserService userService;

  /* method to help fetch the user details based on @PathVariable userId and requires a valid auth token */
  @RequestMapping(value = "/userprofile/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable final long userId, @RequestHeader("authorization") final String authTokenString) 
  throws AuthorizationFailedException, UserNotFoundException {
    /* 1. we first need to check if the auth token given is a valid auth token or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);

    /* 2. here it means that the user requesting for the service is a valid user in the application */
    UserEntity foundEntity = userService.fetchUserById(userId);

    /* 3. build and send the user details to the client side */
    UserDetailsResponse details = this.buildDetails(foundEntity);
    return new ResponseEntity(details, HttpStatus.OK);
  }

  /* an individual method that will help build the final user details response object */
  private UserDetailsResponse buildDetails(UserEntity foundEntity) {
    UserDetailsResponse details = new UserDetailsResponse();
    details.setFirstName(foundEntity.getFirstName());
    details.setLastName(foundEntity.getLastName());
    details.setUserName(foundEntity.getUserName());
    details.setEmailAddress(foundEntity.getEmail());
    details.setCountry(foundEntity.getCountry());
    details.setAboutMe(foundEntity.getAboutMe());
    details.setDob(foundEntity.getDob());
    details.setContactNumber(foundEntity.getContactNumber());
    return details;
  }
}
