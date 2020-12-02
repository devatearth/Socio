package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntitiy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QuestionDao {

    //Creating an instance of EntityManager
    @Autowired
    private EntityManager entityManager;

    //Create a qusestion
    public QuestionEntitiy createQuestion(QuestionEntitiy questionEntitiy){
            entityManager.persist(questionEntitiy);
            return questionEntitiy;
    }

    public List<QuestionEntitiy> getAllQuestions() {
        return entityManager.createNamedQuery("getAllQuestions",QuestionEntitiy.class).getResultList();
    }
}
