package com.upgrad.quora.api.controller;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;

/* java imports */
import java.util.UUID;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

/* app imports */
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.questionEntitiy;
import com.upgrad.quora.service.entity.answerEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerDeleteResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;

/**
  * AnswerController.class is responsible for handling all requests pertaining to answer api
  **/

@RestController
@RequestMapping("/")
public class AnswerController {
  @Autowired
  private UserService userService;
  
  @Autowired
  private QuestionService questionService;

  @Autowired
  private AnswerService answerService;

  /* create answer handler */
  @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", 
  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, 
  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authTokenString, @PathVariable long questionId, @RequestBody AnswerRequest answerRequest) 
  throws AuthorizationFailedException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);

    /* 2. fetch the relevant question based on the question id pathvariable */
    questionEntitiy qEntity = questionService.getQuestionByIdValue(questionId); 

    /* 3. create answer through service */
    int userId = (int) isValidRequestor.getId();
    String answerString = answerRequest.getAnswer();
    String uuid = answerService.createAnswer(answerString, userId, (int) questionId);

    /* 4. send the response back to the client side */
    AnswerResponse answerResponse = new AnswerResponse();
    answerResponse.setId(uuid);
    answerResponse.setStatus("ANSWER CREATED");
    return new ResponseEntity(answerResponse, HttpStatus.OK);
  }

  /* edit answer handler */
  @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", 
  consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, 
  produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswerContent(@RequestHeader("authorization") final String authTokenString, @PathVariable long answerId, @RequestBody AnswerEditRequest answerEditRequest) 
  throws AuthorizationFailedException, AnswerNotFoundException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);

    /* 2. send the request to the service handler from here */
    String uuid = answerService.editAnswerContent((int) isValidRequestor.getId(), (int) answerId, answerEditRequest.getContent());

    /* 3. send the response back to the client side */
    AnswerEditResponse answerEditResponse = new AnswerEditResponse();
    answerEditResponse.setId(uuid);
    answerEditResponse.setStatus("ANSWER EDITED");
    return new ResponseEntity(answerEditResponse, HttpStatus.OK);
  }

  /* delete answer handler */
  @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization") final String authTokenString, @PathVariable long answerId) 
  throws AuthorizationFailedException, AnswerNotFoundException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);

    /* 2. send the request to the service handler from here */
    String uuid = answerService.deleteAnswer(isValidRequestor.getId(), answerId);

    /* 3. send the response back to the client side */
    AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse();
    answerDeleteResponse.setId(uuid);
    answerDeleteResponse.setStatus("ANSWER DELETED");
    return new ResponseEntity(answerDeleteResponse, HttpStatus.OK);
  }

  /* fetches all answers based on question id */
  @RequestMapping(value = "answer/all/{questionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestionId(@RequestHeader("authorization") final String authTokenString, @PathVariable long questionId) 
  throws AuthorizationFailedException, InvalidQuestionException, Exception {
    /* 1. we first need to check whether the jwt token received is ok or not */
    UserEntity isValidRequestor = userService.performAuthTokenValidation(authTokenString);
    
    /* 2. if you are here, it means that we have found a valid requesting user and no exception was thrown */
    /* and perform all answers by qId fetch from the db */
    questionEntitiy question = questionService.getQuestionByIdValue(questionId);
    List<answerEntity> allAnswers = answerService.getAllAnswersToQuestionId((int) questionId);

    /* 3. build the list of questions as per required schema */
    List<AnswerDetailsResponse> aResponse = new ArrayList<AnswerDetailsResponse>();
    for (answerEntity e : allAnswers) {
      AnswerDetailsResponse a = new AnswerDetailsResponse();
      a.setId(e.getUuid());
      a.answerContent(e.getAnswer());
      a.questionContent(question.getContent());
      aResponse.add(a);
    }

    /* 4. finally send the response to the client side */
    return new ResponseEntity(aResponse, HttpStatus.OK);
  }
}
