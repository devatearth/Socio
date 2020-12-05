package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GetAllQuestionsBusinessService {

    //Creating an instance to access database
    @Autowired
    private UserDao userDao;

    //Creating an instance to access DB
    @Autowired
    private UserAuthDao userAuthDao;

    //Creating an instance to access DB
    @Autowired
    private QuestionDao questionDao;

    @Transactional
    public UserAuthEntity performAuthTokenValidation(String authToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthEntityByAccessToken(authToken);

        if (userAuthEntity.getLogoutAt() != null) {
            int difference = userAuthEntity.getLogoutAt().compareTo(ZonedDateTime.now());
            if (difference < 0) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
            }
        }
        return userAuthEntity;
    }

    public List<QuestionEntitiy> getAllQuestions() {
        List<QuestionEntitiy> allQuestions = questionDao.getAllQuestions();
        return allQuestions;
    }
}

