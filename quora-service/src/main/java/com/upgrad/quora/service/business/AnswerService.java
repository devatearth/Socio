package com.upgrad.quora.service.business;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* app imports */
import com.upgrad.quora.service.entity.answerEntity;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;

/* java imports */
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.List;

/**
  * AnswerService.class is responsible for handling all business logic pertaining
  * to the answer api end points
  **/

@Service
public class AnswerService {
  @Autowired
  private AnswerDao answerDao;

  /* helps to create or insert an answer for a question posted by a user */
  @Transactional
  public String createAnswer(String answer, int userId, int questionId) throws Exception {
    try {
      answerEntity aEntity = this.buildAnswerEntity(answer, userId, questionId);
      answerDao.createAnswer(aEntity);
      return aEntity.getUuid();
    }
    catch (Exception e) {
      /* could be some exception that we may have not thought of, keeping a track in case something comes up for a future build requirement */
      System.out.println("Create Answer: Exception Thrown");
      System.out.println(e);
      return null;
    }
  }

  /* helps to edit an existing answer only if the request comes from an owner */
  @Transactional
  public String editAnswerContent(int userId, int answerId, String answer) 
  throws AnswerNotFoundException, AuthorizationFailedException, Exception {
    /* 1. we need to check if we have a record for an answer based on the answer id */
    answerEntity aEntity = answerDao.fetchAnswerById(answerId);
    if (aEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    else if (userId != aEntity.getUserId()) {
      throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
    }
    else {
      String uuid = aEntity.getUuid();
      aEntity.setAnswer(answer);
      return uuid;
    }
  }

  /* helps with the business logic applicable for deleting answer from the db */
  @Transactional
  public String deleteAnswer(final long userId, final long answerId) throws AnswerNotFoundException, AuthorizationFailedException, Exception {
    /* 1. we need to check if we have a record for an answer based on the answer id */
    answerEntity aEntity = answerDao.fetchAnswerById(answerId);
    if (aEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    else if (userId != aEntity.getUserId()) {
      throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can delete the answer");
    }
    else {
      return answerDao.deleteAnswer(aEntity);
    }
  }

  /* method to build an individual anwer entity */
  private answerEntity buildAnswerEntity(String answer, int userId, int questionId) {
    answerEntity a = new answerEntity();
    a.setAnswer(answer);
    a.setUserId(userId);
    a.setQuestionId(questionId);
    a.setUuid(UUID.randomUUID().toString());
    a.setDate(new Timestamp(System.currentTimeMillis()));
    return a;
  }

  /* method to help fetch all the answers based on a question id */
  public List<answerEntity> getAllAnswersToQuestionId(final int questionId) throws InvalidQuestionException, Exception {
    List<answerEntity> answerList = answerDao.getAllAnswersToQuestionId(questionId);
    if (answerList == null) {
      throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
    }
    else {
      return answerList;
    }
  }
}
