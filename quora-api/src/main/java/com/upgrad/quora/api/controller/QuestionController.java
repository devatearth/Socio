package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private CreateQuestionBusinessService createQuestionBusinessService;

    @Autowired
    private GetAllQuestionsBusinessService getAllQuestionsBusinessService;

    @Autowired
    EditQuestionContentBusinessService editQuestionContentBusinessService;

    @Autowired
    DeleteQuestionBusinessService deleteQuestionBusinessService;

    @Autowired
    private GetAllQuestionsByUserBusinessService getAllQuestionsByUserBusinessService;

    /*
    -> createQuestion - "/question/create"

    * This endpoint is used to create a question in the Quora Application which will be shown to all the users. Any user can access this endpoint.
    * It should be a POST request.
    * This endpoint requests for all the attributes in 'QuestionRequest' about the question and access token of the signed in user as a string in the authorization field of the Request
    Header.
    * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
    * If the user has signed out, throw 'AuthorizationFailedException' with the message code- 'ATHR-002' and message -'User is signed out.Sign in first to post a question'.
    * Else, save the question information in the database and return the 'uuid' of the question and message 'QUESTION CREATED' in the JSON response with the corresponding HTTP status.
    * This method is responsible for handling a new question creation request from the client side

    */
    @RequestMapping(path = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authTokenString, QuestionRequest question)
            throws AuthorizationFailedException, Exception {
        /* 1. we first need to check whether the jwt token received is ok or not */
        UserAuthEntity isValidRequestor = createQuestionBusinessService.performAuthTokenValidation(authTokenString);

        /* 2. if you are here, it means that we have found a valid requesting user and no exception was thrown */
        /* and create the question in the database */
        QuestionEntitiy questionEntitiy = new QuestionEntitiy();
        questionEntitiy.setContent(question.getContent());
        questionEntitiy.setDate(ZonedDateTime.now());
        questionEntitiy.setUuid(UUID.randomUUID().toString());
        questionEntitiy.setUserId(isValidRequestor.getUser().getId());
        QuestionEntitiy persistedQuestion = createQuestionBusinessService.createQuestion(questionEntitiy);

        /* 3. finally send the response back to the client side */
        QuestionResponse qResponse = new QuestionResponse();
        qResponse.setId(persistedQuestion.getUuid());
        qResponse.setStatus("QUESTION CREATED");
        return new ResponseEntity(qResponse, HttpStatus.OK);
    }
    /*
     -> getAllQuestions - "/question/all"

     * This endpoint is used to fetch all the questions that have been posted in the application by any user. Any user can access this endpoint.

     * It should be a GET request.
     * This endpoint requests for access token of the signed in user as a string in authorization Request Header.
     * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException' with the message code - 'ATHR-001' and message - 'User has not signed in'.
     * If the user has signed out, throw 'AuthorizationFailedException' with the message code-'ATHR-002' and message-'User is signed out.Sign in first to get all questions'.
     * Else, return 'uuid' and 'content' of all the questions from the database in the JSON response with the corresponding HTTP status.
     */

    //This method will get all the questions
    @RequestMapping(method = RequestMethod.GET, value = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") String accessToken) throws AuthorizationFailedException {
        getAllQuestionsBusinessService.performAuthTokenValidation(accessToken);
        List<QuestionEntitiy> allQuestions = getAllQuestionsBusinessService.getAllQuestions();
        List<QuestionDetailsResponse> questionResponse = new ArrayList<QuestionDetailsResponse>();
        for (QuestionEntitiy e : allQuestions) {
            QuestionDetailsResponse q = new QuestionDetailsResponse();
            q.setId(e.getUuid());
            q.setContent(e.getContent());
            questionResponse.add(q);
        }

        /* 4. finally send the response to the client side */
        return new ResponseEntity(questionResponse, HttpStatus.OK);
    }

    /*

    -> editQuestionContent - "/question/edit/{questionId}"

    * This endpoint is used to edit a question that has been posted by a user. Note, only the owner of the question can edit the question.
    * It should be a PUT request.
    * This endpoint requests for all the attributes in 'QuestionEditRequest', the path variable 'questionId' as a string for the corresponding question which is to be edited in the database and access token of the signed in user as a string in the authorization field of the Request Header.
    * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException' with the message code-'ATHR-001' and message-'User has not signed in'.
    * If the user has signed out, throw 'AuthorizationFailedException' with the message code-'ATHR-002' and message-'User is signed out.Sign in first to edit the question'.
    * Only the question owner can edit the question. Therefore, if the user who is not the owner of the question tries to edit the question throw "AuthorizationFailedException" with the message code-'ATHR-003' and message-'Only the question owner can edit the question'.
    * If the question with uuid which is to be edited does not exist in the database, throw 'InvalidQuestionException' with the message code - 'QUES-001' and message -'Entered question uuid does not exist'.
    * Else, edit the question in the database and return 'uuid' of the edited question and message 'QUESTION EDITED' in the JSON response with the corresponding HTTP status.
    */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(final QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }

        // Creating question entity for further update
        QuestionEntitiy questionEntity = new QuestionEntitiy();
        questionEntity.setContent(questionEditRequest.getContent());
        questionEntity.setUuid(questionId);

        // Return response with updated question entity
        QuestionEntitiy updatedQuestionEntity = editQuestionContentBusinessService.editQuestionContent(questionEntity, bearerToken);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(updatedQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
    /*

    -> deleteQuestion - "/question/delete/{questionId}"

    * This endpoint is used to delete a question that has been posted by a user. Note, only the question owner of the question or admin can delete a question.

    * It should be a DELETE request.
    * This endpoint requests for the path variable 'questionId' as a string for the corresponding question which is to be deleted from the database and access token of the signed in user as a string in the authorization field of the Request Header.
    * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException' with the message code - 'ATHR-001' and message - 'User has not signed in'.
    * If the user has signed out, throw 'AuthorizationFailedException' with the message code- 'ATHR-002' and message -'User is signed out.Sign in first to delete a question'.
    * Only the question owner or admin can delete the question. Therefore, if the user who is not the owner of the question or the role of the user is ‘nonadmin’ and tries to delete the question, throw 'AuthorizationFailedException' with the message code-'ATHR-003' and message -'Only the question owner or admin can delete the question'.
    * If the question with uuid which is to be deleted does not exist in the database, throw 'InvalidQuestionException' with the message code-'QUES-001' and message-'Entered question uuid does not exist'.
    * Else, delete the question from the database and return 'uuid' of the deleted question and message -'QUESTION DELETED' in the JSON response with the corresponding HTTP status.
    */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }

        // Delete requested question
        deleteQuestionBusinessService.userQuestionDelete(questionId, bearerToken);

        // Return response
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }
    /*

    -> getAllQuestionsByUser - "question/all/{userId}"
    
    * This endpoint is used to fetch all the questions posed by a specific user. Any user can access this endpoint.

    * It should be a GET request.
    * This endpoint requests the path variable 'userId' as a string for the corresponding user whose questions are to be retrieved from the database and access token of the signed in user as a string in authorization Request Header.
    * If the access token provided by the user does not exist in the database throw 'AuthorizationFailedException' with the message code-'ATHR-001' and message -'User has not signed in'.
    * If the user has signed out, throw 'AuthorizationFailedException' with the message code-'ATHR-002' and message-'User is signed out.Sign in first to get all questions posted by a specific user'.
    * If the user with uuid whose questions are to be retrieved from the database does not exist in the database, throw 'UserNotFoundException' with the message code -'USR-001' and message -'User with entered uuid whose question details are to be seen does not exist'.
    * Else, return 'uuid' and 'content' of all the questions posed by the corresponding user from the database in the JSON response with the corresponding HTTP status.
    */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        // Logic to handle Bearer <accesstoken>
        // User can give only Access token or Bearer <accesstoken> as input.
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }
        // Get all questions for requested user
        List<QuestionEntitiy> allQuestions = getAllQuestionsByUserBusinessService.getAllQuestionsByUser(userId, bearerToken);

        // Create response
        List<QuestionDetailsResponse> allQuestionDetailsResponse = new ArrayList<QuestionDetailsResponse>();

        for (int i = 0; i < allQuestions.size(); i++) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .content(allQuestions.get(i).getContent())
                    .id(allQuestions.get(i).getUuid());
            allQuestionDetailsResponse.add(questionDetailsResponse);
        }

        // Return response
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponse, HttpStatus.FOUND);
    }


}
