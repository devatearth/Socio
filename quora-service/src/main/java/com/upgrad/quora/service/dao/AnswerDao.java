package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntitiy;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;


    public AnswerEntity getAnswerFromUuid(final String answerUuId) {
        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class).setParameter("uuid", answerUuId).getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAnswersToQuestion(final QuestionEntitiy question) {
        try {
            List<AnswerEntity> allAnswersToQuestion = entityManager.createNamedQuery("getAnswersByQuestionId", AnswerEntity.class).setParameter("question", question).getResultList();
            return allAnswersToQuestion;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteAnswerFromUuid(final String answerId) {
        entityManager.createQuery("delete from AnswerEntity u where u.uuid =:answerId").setParameter("answerId", answerId).executeUpdate();
    }

}