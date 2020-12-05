package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

// @author github.com/vetrivel-muthusamy:
// This class is used to edit an answer. Only the owner of the answer can edit the answer.

@Service
public class EditAnswerService {
    @Autowired
    private UserDao userDao;


    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDao questionDao;

    //Creating a bean to use the feature of authorization
    @Autowired
    private AuthorizationBusinessService authorizationBusinessService;


    @Transactional(propagation = Propagation.REQUIRED)

    //This method is used to edit an already created answer.
    public AnswerEntity editAnswer(final String authorization, final AnswerEntity answerEntity, final String answerId) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity = authorizationBusinessService.ValidateAccessToken(authorization);

        //If the access token provided by the user does not exist in the database throw "AuthorizationFailedException" with the message code - 'ATHR-001' and message - 'User has not signed in'.
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        //If the user has signed out, throw "AuthorizationFailedException" with the message code - 'ATHR-002' and message 'User is signed out.Sign in first to edit an answer'.
        if (userAuthEntity.getLogoutAt() != null) {
            int difference = userAuthEntity.getLogoutAt().compareTo(ZonedDateTime.now());
            if (difference < 0) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
            }
        }
        AnswerEntity answerToBeEdited = answerDao.getAnswerFromUuid(answerId);
        //If the answer with uuid which is to be edited does not exist in the database, throw "AnswerNotFoundException" with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
        if (answerToBeEdited == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        long id1 = answerToBeEdited.getUser().getId();
        long id2 = userAuthEntity.getUser().getId();

        //Only the answer owner can edit the answer. Therefore, if the user who is not the owner of the answer tries to edit the answer throw "AuthorizationFailedException" with the message code - 'ATHR-003' and message - 'Only the answer owner can edit the answer'.
        if (id1 != id2) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerToBeEdited.setAnswer(null);
        answerToBeEdited.setAnswer(answerEntity.getAnswer());

        return answerToBeEdited;
    }
}