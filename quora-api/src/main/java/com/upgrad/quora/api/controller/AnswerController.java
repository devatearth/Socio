package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.CreateAnswerService;
import com.upgrad.quora.service.business.DeleteAnswerService;
import com.upgrad.quora.service.business.EditAnswerService;
import com.upgrad.quora.service.business.GetAllAnswersToQuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//@author github.com/vetrivel-muthusamy

@RestController
@RequestMapping("/")
public class AnswerController {

    //Added by @github.com/vetrivel-muthusamy: Method to create an Answer
    @Autowired
    private CreateAnswerService createAnswerService;
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization, final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity = createAnswerService.createAnswer(authorization, answerEntity, questionId);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    //Added by @github.com/vetrivel-muthusamy: Method to edit the contents of an Answer
    @Autowired
    private EditAnswerService editAnswerService;
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@RequestHeader("authorization") final String authorization, final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAnswer(answerEditRequest.getContent());
        AnswerEntity editedAnswer = editAnswerService.editAnswer(authorization, answerEntity, answerId);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedAnswer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    //Added by @github.com/vetrivel-muthusamy: Method to delete an answer
    @Autowired
    private DeleteAnswerService deleteAnswerService;
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization")final String authorization, @PathVariable("answerId") final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        String deletedAnswerUuid = deleteAnswerService.deleteAnswer(authorization,answerId);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswerUuid).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    //Added by @github.com/vetrivel-muthusamy: Method to get all answers to the question.
    @Autowired
    private GetAllAnswersToQuestionService getAllAnswersToQuestionService;
    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@RequestHeader ("authorization") final String authorization, @PathVariable("questionId") final String questionId) throws AuthorizationFailedException, InvalidQuestionException {
        List<AnswerEntity> allAnswersToQuestion = getAllAnswersToQuestionService.getAllAnswersToQuestion(questionId, authorization);
        AnswerEntity answerEntity;
        List<AnswerDetailsResponse> displayAllAnswersByQuestion = new ArrayList<>();
        for (int i = 0; i < allAnswersToQuestion.size(); i++) {
            answerEntity = allAnswersToQuestion.get(i);
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).questionContent(answerEntity.getQuestion().getContent()).answerContent(answerEntity.getAnswer());
            displayAllAnswersByQuestion.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(displayAllAnswersByQuestion, HttpStatus.OK);
    }
}