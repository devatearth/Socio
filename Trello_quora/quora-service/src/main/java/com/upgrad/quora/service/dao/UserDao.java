package com.upgrad.quora.service.dao;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* app imports */
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;

/* java imports */
import javax.persistence.EntityManager;

@Repository
public class UserDao {
  @Autowired
  private EntityManager entityManager;

  /* method to retrieve a user by the user name */
  public UserEntity getUserByUserName(String userName) {
    try {
        return entityManager.createNamedQuery("getUserByUserId", UserEntity.class).setParameter("userName", userName).getSingleResult();
    } catch (Exception e) {
        return null;
    }
  }

  /* method to retrieve user by an email address */
  public UserEntity getUserByEmail(String email) {
    try {
        return entityManager.createNamedQuery("getUserByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
    } catch (Exception e) {
        return null;
    }
  }

  /* method to help register a particular user in the database */
  public UserEntity RegisterUser(UserEntity newUser) {
    entityManager.persist(newUser);
    return newUser;
  }

  /* method to help create auth token */
  public UserAuthEntity createAuthToken(UserAuthEntity userAuthTokenEntity) {
    entityManager.persist(userAuthTokenEntity);
    return userAuthTokenEntity;
  }

  /* performs the necessary updates for the user */
  public void updateUser(final UserEntity updatedUserEntity){
    entityManager.merge(updatedUserEntity);
  }

  /* helps to locate a particular user by the auth token */
  public UserAuthEntity findUserByThisAuthToken(final String accessToken) throws SignOutRestrictedException {
    return entityManager.createNamedQuery("fetchThisUserByJWTAuthToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
  }
}
