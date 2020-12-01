package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

/*
This class implements the access to DB
 */
@Repository
public class UserDao {

    //Creating an instance of EntityManager
    @Autowired
    private EntityManager entityManager;

    //This method will take the UserName as input and returns the User object, If exist in DB else returns null
    public UserEntity getUserByUserName(String userName) {
        try {
            return entityManager.createNamedQuery("getUserByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    //This method will take the email as input and returns the User object, If exists in DB else returns null.
    public UserEntity getUserByEmail(String email) {
        try {
            return entityManager.createNamedQuery("getUserByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    //This method will persist the User object to DB
    public UserEntity RegisterUser(UserEntity newUser) {
        System.out.println(newUser);
        entityManager.persist(newUser);
        return newUser;
    }

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
    public void updateUserAuthEntity(UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
    }

    //This method will take the UserName as input and returns the User object, If exist in DB else returns null
    public UserEntity getUserByUserId(String userId) {
        try {
            return entityManager.createNamedQuery("getUserByUserId", UserEntity.class).setParameter("userId", userId).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    //This method will delete a user
    public void deleteUser(UserEntity user){
        entityManager.remove(user);
    }

}
