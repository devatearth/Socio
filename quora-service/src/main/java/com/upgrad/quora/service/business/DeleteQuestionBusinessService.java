package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class DeleteQuestionBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public void userQuestionDelete(final String questionId, final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserByAuthtoken(authorization);
        QuestionEntitiy questionEntitiy = new QuestionEntitiy();

        // Validate if user is signed in or not &  Validate if user has signed out in else if

        int difference = userAuthEntity.getLogoutAt().compareTo(ZonedDateTime.now());

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (difference < 0) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to delete a question");
        }


        // Validate if requested question exist or not
        if (questionDao.getQuestionByQUuid(questionId) == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        // Validate if current user is the owner of requested question or the role of user is not nonadmin
        if (!userAuthEntity.getUser().getUuid().equals(questionDao.getQuestionByQUuid(questionEntitiy.getUuid()))) {
            if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        questionDao.deleteQuestion(questionId);
    }


}
