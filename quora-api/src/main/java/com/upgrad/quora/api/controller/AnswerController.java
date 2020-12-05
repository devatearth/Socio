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

    /*

    -> createAnswer - "/question/{questionId}/answer/create"

    * This endpoint is used to create an answer to a particular question. Any user can access this endpoint.
    * It should be a POST request.
    * This endpoint requests for the attribute in "Answer Request", the path variable 'questionId ' as a string for the corresponding question which is to be answered in the database and access token of the signed in user as a string in authorization Request Header.
    * If the question uuid entered by the user whose answer is to be posted does not exist in the database, throw "InvalidQuestionException" with the message code - 'QUES-001' and message - 'The question entered is invalid'.
    * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
    * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to post an answer'.
    * Else, save the answer information in the database and return the "uuid" of the answer and message "ANSWER CREATED" in the JSON response with the corresponding HTTP status.
    */
    @Autowired
    private CreateAnswerService createAnswerService;
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(AnswerRequest answerRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        //answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity = createAnswerService.createAnswer(answerRequest.getAnswer(),questionId,authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }
    /*

    -> editAnswerContent - "/answer/edit/{answerId}"

    * This endpoint is used to edit an answer. Only the owner of the answer can edit the answer.

    * It should be a PUT request.
    * This endpoint requests for all the attributes in "AnswerEditRequest", the path variable 'answerId' as a string for the corresponding answer which is to be edited in the database and access token of the signed in user as a string in authorization Request Header.
    * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
    * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002' and message 'User is signed out.Sign in first to edit an answer'.
    * Only the answer owner can edit the answer. Therefore, if the user who is not the owner of the answer tries to edit the answer throw "AuthorizationFailedException" with the message code - 'ATHR-003' and message - 'Only the answer owner can edit the answer'.
    * If the answer with uuid which is to be edited does not exist in the database, throw "AnswerNotFoundException" with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
    * Else, edit the answer in the database and return "uuid" of the edited answer and message "ANSWER EDITED" in the JSON response with the corresponding HTTP status.

    */
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
    /*

    -> deleteAnswer - "/answer/delete/{answerId}"

    * This endpoint is used to delete an answer. Only the owner of the answer or admin can delete an answer.

    * It should be a DELETE request.
    * This endpoint requests for the path variable 'answerId' as a string for the corresponding answer which is to be deleted from the database and access token of the signed in user as a string in authorization Request Header.
    * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
    * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to delete an answer'.
    * Only the answer owner or admin can delete the answer. Therefore, if the user who is not the owner of the answer or the role of the user is ‘nonadmin’ and tries to delete the answer throw "AuthorizationFailedException" with the message code - 'ATHR-003' and message - 'Only the answer owner or admin can delete the answer'.
    * If the answer with uuid which is to be deleted does not exist in the database, throw "AnswerNotFoundException" with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
    * Else, delete the answer from the database and return "uuid" of the deleted answer and message "ANSWER DELETED" in the JSON response with the corresponding HTTP status.
    */
    //Added by @github.com/vetrivel-muthusamy: Method to delete an answer
    @Autowired
    private DeleteAnswerService deleteAnswerService;
    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@RequestHeader("authorization")final String authorization, @PathVariable("answerId") final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {
        String deletedAnswerUuid = deleteAnswerService.deleteAnswer(authorization,answerId);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswerUuid).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }
    /*

    -> getAllAnswersToQuestion - "answer/all/{questionId}"

    * This endpoint is used to get all answers to a particular question. Any user can access this endpoint.

    * It should be a GET request.
    * This endpoint requests the path variable 'questionId' as a string for the corresponding question whose answers are to be retrieved from the database and access token of the signed in user as a string in authorization Request Header.
    * If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
    * If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to get the answers'.
    * If the question with uuid whose answers are to be retrieved from the database does not exist in the database, throw "InvalidQuestionException" with the message code - 'QUES-001' and message - 'The question with entered uuid whose details are to be seen does not exist'.
    * Else, return "uuid" of the answer, "content" of the question and "content" of all the answers posted for that particular question from the database in the JSON response with the corresponding HTTP status.
    */
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