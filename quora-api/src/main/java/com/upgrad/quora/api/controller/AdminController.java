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
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.api.model.UserDeleteResponse;

/**
  * AdminController.class is responsible for handling all methods that would
  * be granted to users if they are marked as 'admin'
  */

@RestController
@RequestMapping("/")
public class AdminController {
  @Autowired
  private UserService userService;
  
  /* method to help delete a user from the database */
  @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable final long userId, @RequestHeader("authorization") final String authTokenString) 
  throws AuthorizationFailedException, UserNotFoundException {
    /* 1. we first need to check if the auth token given is a valid auth token or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);

    /* 2. if not admin, then do not allow for futher beyond this point! */
    if (!isValidRequestor.getRole().equals("admin")) {
      throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
    }

    /* 3. if you are here, it means that we have a valid admin user and shall proceed to see if we can
       perform the delete request */
    UserEntity deletedEntity = userService.performDelete(isValidRequestor, userId);

    /* 4. return response to the client post delete is complete */
    UserDeleteResponse deletedResponse = new UserDeleteResponse();
    deletedResponse.setStatus("USER SUCCESSFULLY DELETED");
    deletedResponse.setId(deletedEntity.getUuid());
    return new ResponseEntity(deletedResponse, HttpStatus.OK);
  }
}
