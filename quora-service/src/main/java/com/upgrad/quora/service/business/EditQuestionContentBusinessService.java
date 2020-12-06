package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class EditQuestionContentBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    //Creating a bean to use the feature of authorization
    @Autowired
    private AuthorizationBusinessService authorizationBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntitiy editQuestionContent(final QuestionEntitiy questionEntity, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = authorizationBusinessService.ValidateAccessToken(authorizationToken);

        // Validate if user is signed in or not &  Validate if user has signed out in else if
        if (userAuthEntity.getLogoutAt() != null) {
            int difference = userAuthEntity.getLogoutAt().compareTo(ZonedDateTime.now());
            if (difference < 0) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
            }
        }
        // Validate if requested question exist or not
        QuestionEntitiy existingQuestionEntity = questionDao.getQuestionFromUuid(questionEntity.getUuid());
        if (existingQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        // Validate if current user is the owner of requested question
        UserEntity currentUser = userAuthEntity.getUser();
        //UserEntity questionOwner = questionDao.getQuestionByQUuid(questionEntity.getUuid()).getUser();
        if (currentUser.getUuid() != existingQuestionEntity.getUser().getUuid()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        // After all condtions passed then we are setting the required attrbutes for the question entity
        questionEntity.setId(existingQuestionEntity.getId());
        questionEntity.setUser(existingQuestionEntity.getUser());
        questionEntity.setDate(existingQuestionEntity.getDate());

        return questionDao.updateQuestion(questionEntity);
    }


}
