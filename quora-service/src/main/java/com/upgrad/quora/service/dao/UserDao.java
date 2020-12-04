package com.upgrad.quora.service.dao;

/* spring imports */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* app imports */
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;

/* java imports */
import javax.persistence.EntityManager;

/** 
  * UserDao is responsible to handle all database queries pertaining to a user entity (or user!)
  **/

@Repository
public class UserDao {
  @Autowired
  private EntityManager entityManager;

  /* method to retrieve a user by the user name */
  public UserEntity getUserByUserName(String userName) {
    try {
      return entityManager.createNamedQuery("getUserByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
    } 
    catch (Exception e) {
      System.out.println("getUserByUserName() Exception Raised");
      System.out.println(e);
      return null;
    }
  }

  /* method to retrieve user by an email address */
  public UserEntity getUserByEmail(String email) {
    try {
      return entityManager.createNamedQuery("getUserByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
    }
    catch (Exception e) {
      System.out.println("getUserByEmail Exception Raised");
      System.out.println(e);
      return null;
    }
  }

  /* method to help register a particular user in the database */
  public UserEntity RegisterUser(UserEntity newUser) {
    try {
      entityManager.persist(newUser);
      return newUser;
    }
    catch (Exception e) {
      System.out.println("Register User Exception Thrown");
      System.out.println(e);
      return null;
    }
  }

  /* method to help create auth token */
  public UserAuthEntity createAuthToken(UserAuthEntity userAuthTokenEntity) {
    try {
      entityManager.persist(userAuthTokenEntity);
      return userAuthTokenEntity;
    }
    catch (Exception e) {
      System.out.println("Create Auth Token Exception Thrown");
      System.out.println(e);
      return null;
    }
  }

  /* performs the necessary updates for the user */
  public void updateUser(final UserEntity updatedUserEntity){
    try {
      entityManager.merge(updatedUserEntity);
    }
    catch (Exception e) {
      /* this is just in case if something goes wrong! */
      System.out.println("Update User Exception Thrown.");
      System.out.println(e);
    }
  }

  /* helps to locate a particular user by the auth token */
  public UserAuthEntity findUserByThisAuthToken(final String accessToken) {
    try {
      return entityManager.createNamedQuery("fetchThisUserByJWTAuthToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
    }
    catch (Exception e) {
      System.out.println("Find User By This Auth Token: Exception Raised");
      System.out.println(e);
      return null;
    }
  }

  /* helps to locate a single user entity based on the id value that has been given from service */
  public UserEntity fetchUserById(final long id) {
    try {
      return entityManager.createNamedQuery("getUserByUserId", UserEntity.class).setParameter("userId", id).getSingleResult();
    }
    catch (Exception e) {
      System.out.println(": Exception Raised @ Fetch User By Id " + e);
      return null;
    }
  }

  /* helps to delete a single user entity from the database upon the id */
  public UserEntity performDelete(UserEntity admin, final long userId) throws AuthorizationFailedException {
    /* again just a precaution */
    if (!admin.getRole().equals("admin")) {
      throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
    }
    else {
      UserEntity toBeDeleted = this.fetchUserById(userId);
      this.entityManager.remove(toBeDeleted);
      return toBeDeleted;
    }
  }
}
