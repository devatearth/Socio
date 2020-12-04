package com.upgrad.quora.service.dao;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* app imports */
import com.upgrad.quora.service.entity.answerEntity;

/* java imports */
import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class AnswerDao {
  @Autowired
  private EntityManager entityManager;

  /* method to retrieve a user by the user name */
  public void createAnswer(answerEntity aEntity) throws Exception {
    try {
      entityManager.persist(aEntity);
    }
    catch (Exception e) {
      /* this is just in case if something goes wrong! */
      System.out.println("Create Answer: Exception Thrown");
      System.out.println(e);
    }
  }

  /* checks to see if an answer entity is available based on id */
  public answerEntity fetchAnswerById(final long answerId) throws Exception {
    try {
      return entityManager.createNamedQuery("performAnswerGetByIdQuery", answerEntity.class).setParameter("answerId", answerId).getSingleResult();
    }
    catch (Exception e) {
      System.out.println("Fetch Answer By Id: Exception Thrown");
      System.out.println(e);
      return null;
    }
  }

  /* method to update an existing answer in the db */
  public void updateAnswer(answerEntity aEntity) throws Exception {
    try {
      entityManager.merge(aEntity);
    }
    catch (Exception e) {
      /* this is just in case if something goes wrong! */
      System.out.println("Update Answer: Exception Thrown.");
      System.out.println(e);
    }
  }

  /* method to delete an existing answer in the db */
  public String deleteAnswer(answerEntity aEntity) throws Exception {
    try {
      this.entityManager.remove(aEntity);
      return aEntity.getUuid();
    }
    catch (Exception e) {
      System.out.println("Delete Answer: Exception Thrown");
      System.out.println(e);
      return null;
    }
  }

  /* method to help fetch all answers based on a questionId */
  public List<answerEntity> getAllAnswersToQuestionId(final int questionId) throws Exception {
    try {
      return entityManager.createNamedQuery("performFetchAllByQuestionId", answerEntity.class).setParameter("questionId", (int) questionId).getResultList();
    }
    catch (Exception e) {
      System.out.println("Get All Answers to Question Id: Exception Thrown");
      System.out.println(e);
      return null;
    }
  }
}
