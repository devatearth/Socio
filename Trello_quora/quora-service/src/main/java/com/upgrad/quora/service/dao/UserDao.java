package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class UserDao {

    @Autowired
    private EntityManager entityManager;

    public UserEntity getUserByUserName(String userName){
            try{
                return entityManager.createNamedQuery("getUserByUserId", UserEntity.class).setParameter("userName",userName).getSingleResult();
            }catch(Exception e){
                return null;
            }
    }

    public UserEntity getUserByEmail(String email){
        try{
            return entityManager.createNamedQuery("getUserByEmail", UserEntity.class).setParameter("email",email).getSingleResult();
        }catch(Exception e){
            return null;
        }
    }
    public UserEntity RegisterUser(UserEntity newUser){
        System.out.println(newUser);
        entityManager.persist(newUser);
        return newUser;
    }
}
