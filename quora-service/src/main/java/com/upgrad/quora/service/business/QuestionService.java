package com.upgrad.quora.service.business;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* app imports */
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.questionEntitiy;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

/* java imports */
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.List;

/**
  * QuesitonService.class is responsible for handling all business logic pertaining
  * to the question api end points
  **/

@Service
public class QuestionService {
  @Autowired
  private QuestionDao questionDao;

  @Autowired
  private UserDao userDao;

  /* helps to handle the business logic pertaining to create question. finally,
     will help to send a request to controller to create the question in the db */
  @Transactional
  public String createQuestion(String question, UserEntity creator) throws Exception {
    try {
      questionEntitiy q = this.buildquestionEntitiy(question, creator);
      questionDao.createQuestion(q);
      return q.getUuid();
    }
    catch (Exception e) {
      /* could be some exception that we may have not thought of, keeping a track in case something comes up for a future build requirement */
      System.out.println("Create Question Exception Thrown");
      System.out.println(e);
      return null;
    }
  }

  /* method sends the fetch request to the dao for all questions list */
  public List<questionEntitiy> getAllQuestions() throws Exception {
    return questionDao.getAllQuestions(); 
  }

  /* method sends the request to the dao for getting a single question entity */
  public questionEntitiy getQuestionByIdValue(long qId) throws InvalidQuestionException, Exception {
    questionEntitiy qE = questionDao.getQuestionByIdValue(qId);
    if (qE == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }
    else {
      return qE;
    }
  }

  /* method sends the request to the dao for updating question content */
  @Transactional
  public void editQuestionContent(questionEntitiy updatedQuestion) {
    questionDao.editQuestionContent(updatedQuestion);
  }

  /* method helps to handle the delete request for a question */
  @Transactional
  public String deleteQuestion(final long questionId, UserEntity user) throws AuthorizationFailedException, Exception {
    questionEntitiy question = this.getQuestionByIdValue(questionId);

    final long userId = user.getId();
    final long qUserId =  question.getUserId();
    if (userId != qUserId) {
      throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
    }
    else {
      return questionDao.deleteQuestion(question);
    }
  }

  /* method to help get all questions pertaining to a particular user id if the user id is existing in the db */
  public List<questionEntitiy> getAllQuestionsByUser(final long userId) throws Exception {
    if (userDao.fetchUserById(userId) == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
    }
    else {
      return questionDao.getAllQuestionsByUser(userId); 
    }
  }

  /* individual method that helps build a question entity */
  public questionEntitiy buildquestionEntitiy(String question, UserEntity creator) {
    questionEntitiy q = new questionEntitiy();
    q.setContent(question);
    q.setDate(new Timestamp(System.currentTimeMillis()));
    q.setUuid(UUID.randomUUID().toString());
    q.setUserId((int) creator.getId());
    return q;
  }
}
