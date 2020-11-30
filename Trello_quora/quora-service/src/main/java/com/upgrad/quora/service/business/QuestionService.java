package com.upgrad.quora.service.business;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* app imports */
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.questionEntitiy;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

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

  /* helps to handle the business logic pertaining to create question. finally,
     will help to send a request to controller to create the question in the db */
  @Transactional
  public String createQuestion(String question, UserEntity creator) throws Exception {
    questionEntitiy q = this.buildquestionEntitiy(question, creator);
    questionDao.createQuestion(q);
    return q.getUuid();
  }

  /* method sends the fetch request to the dao for all questions list */
  public List<questionEntitiy> getAllQuestions() throws Exception {
    return questionDao.getAllQuestions(); 
  }

  /* method sends the request to the dao for getting a single question entity */
  public questionEntitiy getQuestionByIdValue(long qId) throws Exception {
    return questionDao.getQuestionByIdValue(qId);
  }

  /* method sends the request to the dao for updating question content */
  @Transactional
  public void editQuestionContent(questionEntitiy updatedQuestion) {
    questionDao.editQuestionContent(updatedQuestion);
  }

  /* method helps to handle the delete request for a question */
  @Transactional
  public String deleteQuestion(final long questionId, UserEntity user) throws Exception {
    questionEntitiy question = this.getQuestionByIdValue(questionId);

    final long userId = user.getId();
    final long qUserId =  question.getUserId();
    if (userId == qUserId) {
      return questionDao.deleteQuestion(question);
    }
    else {
      return null;
    }
  }

  public List<questionEntitiy> getAllQuestionsByUser(final long userId) throws Exception {
    return questionDao.getAllQuestionsByUser(userId); 
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
