package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CreateQuestionBusinessService;
import com.upgrad.quora.service.business.GetAllQuestionsBusinessService;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
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

    /* this method is responsible for handling a new question creation request from the client side */
    @RequestMapping(path = "/question/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authTokenString,QuestionRequest question)
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

    //This method will get all the questions
    @RequestMapping(method = RequestMethod.GET,value = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

}
