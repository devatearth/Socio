package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;


// @author github.com/vetrivel-muthusamy:
// This class is used to delete a answer that has been posted by a user. Note, only the answer owner of the answer or admin can delete a answer.

@Service
public class DeleteAnswerService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    //Creating a bean to use the feature of authorization
    @Autowired
    private AuthorizationBusinessService authorizationBusinessService;

    @Transactional(propagation = Propagation.REQUIRED)

    //This method delete the answer from the database and return "uuid" of the deleted answer.

    public String deleteAnswer(final String authorization, final String answerId) throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthEntity userAuthTokenEntity = authorizationBusinessService.ValidateAccessToken(authorization); //all user details

        if (userAuthTokenEntity.getLogoutAt() != null) {
            int difference = userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now());
            if (difference < 0) {
                throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
            }
        }

        AnswerEntity answerEntity = answerDao.getAnswerFromUuid(answerId); //all attributes of answer

        //If the answer with uuid which is to be deleted does not exist in the database, throw "AnswerNotFoundException" with the message code - 'ANS-001' and message - 'Entered answer uuid does not exist'.
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        UserEntity userEntity = userDao.getUserFromUuid(userAuthTokenEntity.getUuid());
        UserEntity userEntityFromAnswer = answerEntity.getUser();

        //Only the answer owner or admin can delete the answer. Therefore, if the user who is not the owner of the answer or the role of the user is ‘nonadmin’ and tries to delete the answer throw "AuthorizationFailedException" with the message code - 'ATHR-003' and message - 'Only the answer owner or admin can delete the answer'.
        if (!(userEntity.getUuid().equals(userEntityFromAnswer.getUuid())) && !(userEntity.getRole().equals("admin"))) {

            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }

        String deletedAnswerUuid = answerEntity.getUuid();

        answerDao.deleteAnswerFromUuid(deletedAnswerUuid);
        return deletedAnswerUuid; //return deleted Answer's UUid
    }
}