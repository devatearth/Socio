package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

// @author github.com/vetrivel-muthusamy:
// This class defines the Business logic for creating an Answer Feature, and throws defined exceptions.

@Service
public class CreateAnswerService {

    //Creating a bean to use the feature of authorization
    @Autowired
    private AuthorizationBusinessService authorizationBusinessService;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)

    /**
     * Method createAnswer is used to create an answer to a particular question.
     * This method requests for the attribute in "Answer Request", the path variable 'questionId ' as a string for the corresponding question
     which is to be answered in the database and access token of the signed in user as a string in authorization Request Header.
     */

    public AnswerEntity createAnswer(final String answer, final String questionId, final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = authorizationBusinessService.ValidateAccessToken(accessToken);
        QuestionEntitiy questionEntity = questionDao.getQuestionFromUuid(questionId);
        AnswerEntity answerEntity = new AnswerEntity();
        if (userAuthEntity.getLogoutAt() != null) {
            int difference = userAuthEntity.getLogoutAt().compareTo(ZonedDateTime.now());
            if (difference < 0) {
                throw new AuthorizationFailedException(
                        "ATHR-002", "User is signed out.Sign in first to post an answer");
            }
        }
        if (questionEntity == null) {
            throw new InvalidQuestionException(
                    "QUES-001", "The question entered is invalid");
        }
        answerEntity.setUser(userAuthEntity.getUser());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setAnswer(answer);
        return answerDao.createAnswer(answerEntity);


    }

}