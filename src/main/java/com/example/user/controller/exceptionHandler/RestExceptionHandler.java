package com.example.user.controller.exceptionHandler;

import com.example.user.controller.exceptionHandler.dto.ErrorMsgDto;
import com.example.user.controller.exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity DataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.debug("handling exception::" + ex);
        return new ResponseEntity(new ErrorMsgDto(new Date(), "duplicate nickname", ex.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({WebExchangeBindException.class})
    ResponseEntity webExchangeBindException(WebExchangeBindException ex)
    {
        String rejectedValue = ex.getBindingResult().getFieldError().getRejectedValue() == null? ""
                : ex.getBindingResult().getFieldError().getRejectedValue().toString();
        String message = ex.getBindingResult().getFieldError().getDefaultMessage() == null? ""
                :ex.getBindingResult().getFieldError().getDefaultMessage();

        return new ResponseEntity(
                new ErrorMsgDto(new Date(), "bad request ", rejectedValue
                        + " is not valid with message : "
                        + message),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({ ServerWebInputException.class})
    ResponseEntity serverWebInputException(ServerWebInputException ex)
    {

        return new ResponseEntity(
                new ErrorMsgDto(new Date(), "bad request ", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({ UserNotFoundException.class})
    ResponseEntity userNotFoundException(UserNotFoundException ex)
    {

        return new ResponseEntity(
                new ErrorMsgDto(new Date(), "user not found", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
