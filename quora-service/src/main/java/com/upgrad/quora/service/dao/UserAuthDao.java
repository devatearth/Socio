package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserAuthDao {

    //Creating an instance of EntityManager
    @Autowired
    private EntityManager entityManager;


    //This method perist the UserAuthEntity
    public void createAuthToken(UserAuthEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
    }

    //this method will get the UserAuthEntity by accessToken
    public UserAuthEntity getUserAuthEntityByAccessToken(String accessToken) {
        try {
            return entityManager.createNamedQuery("getUserAuthByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    //This method will update the userAuthEntity
    public void updateUserAuthEntity(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
    }

}
