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
import org.springframework.web.bind.annotation.RequestBody;

/* app imports */
import com.upgrad.quora.service.entity.questionEntitiy;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionEditRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.QuestionEditResponse;
import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;

/* java imports */
import java.util.List;
import java.util.ArrayList;

/**
  * QuestionController.class is responsible for handling all api endpoints
  * pertaining to the questions api end point
  **/

@RestController
@RequestMapping("/")
public class QuestionController {
  @Autowired
  private UserService userService;

  @Autowired
  private QuestionService questionService;

  /* this method is responsible for handling a new question creation request from the client side */
  @RequestMapping(value = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authTokenString, QuestionRequest question) 
  throws AuthorizationFailedException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);
    
    /* 2. if you are here, it means that we have found a valid requesting user and no exception was thrown */
    /* and create the question in the database */
    String qUuid = questionService.createQuestion(question.getContent(), isValidRequestor);

    /* 3. finally send the response back to the client side */
    QuestionResponse qResponse = new QuestionResponse();
    qResponse.setId(qUuid);
    qResponse.setStatus("QUESTION CREATED");
    return new ResponseEntity(qResponse, HttpStatus.OK);
  }

  /* this method is responsible for fetching all the questions of all users from the db */
  @RequestMapping(value = "/question/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authTokenString) 
  throws AuthorizationFailedException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);
    
    /* 2. if you are here, it means that we have found a valid requesting user and no exception was thrown */
    /* and perform all questions fetch from the db */
    List<questionEntitiy> allQuestions = questionService.getAllQuestions();

    /* 3. build the list of questions as per required schema */
    List<QuestionDetailsResponse> qResponse = new ArrayList<QuestionDetailsResponse>();
    for (questionEntitiy e : allQuestions) {
      QuestionDetailsResponse q = new QuestionDetailsResponse();
      q.setId(e.getUuid());
      q.setContent(e.getContent());
      qResponse.add(q);
    }

    /* 4. finally send the response to the client side */
    return new ResponseEntity(qResponse, HttpStatus.OK);
  }

  /* this method is responsible for editing an existing question, which can be done only by the owner of that question */
  @RequestMapping(value = "/question/edit/{questionId}", method = RequestMethod.PUT,
  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader("authorization") final String authTokenString, @PathVariable long questionId, @RequestBody QuestionEditRequest editRequest) 
  throws AuthorizationFailedException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not, and then get the existing 
     question entity frm the database */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);
    questionEntitiy currentQuestion = questionService.getQuestionByIdValue(questionId);    

    /* 2. compare the requestor id and the question's user id, they must match to make changes with the request */
    final long requestorId = isValidRequestor.getId();
    final long questionUserId = currentQuestion.getUserId();
    if (requestorId != questionUserId) {
      throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
    }

    /* 3. fetch the content from the edit request and then send the request to perform update */
    currentQuestion.setContent(editRequest.getContent());
    questionService.editQuestionContent(currentQuestion);
    
    /* 4. send the response to the client side */
    QuestionEditResponse qResponse = new QuestionEditResponse();
    qResponse.setId(currentQuestion.getUuid());
    qResponse.setStatus("QUESTION EDITED");
    return new ResponseEntity(qResponse, HttpStatus.OK);
  }

  /* this method is responsible for deleting an existing question */
  @RequestMapping(value = "/question/delete/{questionId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authTokenString, @PathVariable long questionId) 
  throws AuthorizationFailedException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not, and then get the existing 
     question entity frm the database */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);

    /* 2. perform the delete operation through service */
    String uuid = questionService.deleteQuestion(questionId, isValidRequestor);

    /* 3. send the response to the client side */
    QuestionDeleteResponse qResponse = new QuestionDeleteResponse();
    qResponse.setId(uuid);
    qResponse.setStatus("'QUESTION DELETED");
    return new ResponseEntity(qResponse, HttpStatus.OK);
  }

  /* this method is responsible or getting questions of a particular user through user id */
  @RequestMapping(value = "/question/all/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorization") final String authTokenString, @PathVariable long userId) 
  throws AuthorizationFailedException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);
    
    /* 2. if you are here, it means that we have found a valid requesting user and no exception was thrown */
    /* and perform all questions fetch from the db */
    List<questionEntitiy> allQuestions = questionService.getAllQuestionsByUser(userId);

    /* 3. build the list of questions as per required schema */
    List<QuestionDetailsResponse> qResponse = new ArrayList<QuestionDetailsResponse>();
    for (questionEntitiy e : allQuestions) {
      QuestionDetailsResponse q = new QuestionDetailsResponse();
      q.setId(e.getUuid());
      q.setContent(e.getContent());
      qResponse.add(q);
    }

    /* 4. finally send the response to the client side */
    return new ResponseEntity(qResponse, HttpStatus.OK);
  }
}
