package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

// @author github.com/vetrivel-muthusamy:
// This class defines the Business logic for creating an Answer Feature, and throws defined exceptions.

@Service
public class CreateAnswerService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * Method createAnswer is used to create an answer to a particular question.
     * This method requests for the attribute in "Answer Request", the path variable 'questionId ' as a string for the corresponding question
     which is to be answered in the database and access token of the signed in user as a string in authorization Request Header.
     */

    public AnswerEntity createAnswer(final String authorization, final AnswerEntity answerEntity, String questionId) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userDao.getUserByAuthtoken(authorization);
        QuestionEntitiy questionEntity = questionDao.getQuestionFromUuid(questionId);

        //If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002' and message - 'User is signed out.Sign in first to post an answer'.
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        //If the question uuid entered by the user whose answer is to be posted does not exist in the database, throw "InvalidQuestionException"
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }

        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setUser(userAuthEntity.getUser());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setDate(ZonedDateTime.now());

        answerDao.createAnswer(answerEntity);
        return answerEntity;
    }
}