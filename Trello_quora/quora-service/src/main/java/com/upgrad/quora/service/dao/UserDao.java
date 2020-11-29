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

@Repository
public class UserDao {
  @Autowired
  private EntityManager entityManager;

  /* method to retrieve a user by the user name */
  public UserEntity getUserByUserName(String userName) {
    try {
        return entityManager.createNamedQuery("getUserByUserName", UserEntity.class).setParameter("userName", userName).getSingleResult();
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
  public UserAuthEntity findUserByThisAuthToken(final String accessToken) {
    return entityManager.createNamedQuery("fetchThisUserByJWTAuthToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
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
