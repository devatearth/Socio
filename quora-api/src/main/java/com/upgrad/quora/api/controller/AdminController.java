package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminBusinessService adminBusinessService;
    /*
    * This endpoint is used to delete a user from the Quora Application. Only an admin is authorized to access this endpoint.
    * It should be a DELETE request.
    * This endpoint requests the path variable 'userId' as a string for the corresponding user which is to be deleted
      from the database and access token of the signed in user as a string in authorization Request Header.
    * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException' with
      the message code-'ATHR-001' and message -'User has not signed in'.
    * If the user has signed out, throw 'AuthorizationFailedException' with the message code- 'ATHR-002' and message
      -'User is signed out'.
    * If the role of the user is 'nonadmin',  throw 'AuthorizationFailedException' with the message code-'ATHR-003'
      and message -'Unauthorized Access, Entered user is not an admin'.
    * If the user with uuid whose profile is to be deleted does not exist in the database, throw 'UserNotFoundException'
      with the message code -'USR-001' and message -'User with entered uuid to be deleted does not exist'.
    * Else, delete the records from all the tables related to that user and return 'uuid' of the deleted user from
      'users' table and message 'USER SUCCESSFULLY DELETED' in the JSON response with the corresponding HTTP status.
     */
    @RequestMapping(path = "/admin/user/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@RequestParam("userId") String userId, @RequestHeader("authorization") String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        //Validating the access token
        UserAuthEntity validAuth = adminBusinessService.ValidateAccessToken(accessToken);
        System.out.println(validAuth.getUser().getRole());
        if(validAuth.getUser().getRole().equals("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }
        adminBusinessService.deleteUser(userId);
        UserDeleteResponse userResponse = new UserDeleteResponse();
        userResponse.setId(validAuth.getUuid());
        userResponse.setStatus("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userResponse,HttpStatus.OK);

    }
}
