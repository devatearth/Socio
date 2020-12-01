package com.upgrad.quora.api.exception;

/* spring imports */
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/* app imports */
import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.exception.InvalidQuestionException;

/**
  * RestExceptionHandler.class is responsible to handle all exceptions that are being thrown in the
  * program. This will then provide a suitable error response object to the client side 
  **/

@ControllerAdvice
public class RestExceptionHandler {
  /* SignUpRestrictedException */
  @ExceptionHandler(SignUpRestrictedException.class)
  public ResponseEntity<ErrorResponse> signiFailed(SignUpRestrictedException exc, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
      new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
  }

  /* AuthenticationFailedException */
  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exc, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
            new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
  }

  /* SignOutRestrictedException */
  @ExceptionHandler(SignOutRestrictedException.class)
  public ResponseEntity<ErrorResponse> signOutFailed(SignOutRestrictedException exc, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
            new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
  }

  /* AuthorizationFailedException */
  @ExceptionHandler(AuthorizationFailedException.class)
  public ResponseEntity<ErrorResponse> authFailure(AuthorizationFailedException exc, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
            new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
  }

  /* UserNotFoundExceptioni */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> userNotFoundFailure(UserNotFoundException exc, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
            new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
  }

  /* InvalidQuestionException */
  @ExceptionHandler(InvalidQuestionException.class)
  public ResponseEntity<ErrorResponse> invalidQuestionFailure(InvalidQuestionException exc, WebRequest request) {
    return new ResponseEntity<ErrorResponse>(
            new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED);
  }
}
