package com.fortune.product.advice;

import com.fortune.ApiErrorResponse;
import com.fortune.CustomError;
import com.fortune.CustomException;
import com.fortune.ServerError;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalControllerAdvice {

//    @ExceptionHandler(MessagingException.class)
//    public ResponseEntity<EmailServiceError> emailServiceNotSent(){
//        return ResponseEntity.internalServerError().body(
//                new EmailServiceError(ServerError.ERROR_OCCURRED,"An error occured on the server"));
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<Map<String,String>>> methodArgumentNotValid(MethodArgumentNotValidException ex){
        Map<String,String> map= new HashMap<>();
        for(FieldError error:ex.getBindingResult().getFieldErrors()){
            map.put(error.getField(),error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(new ApiErrorResponse<>(map));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse<CustomError<ServerError,String>>> customException(CustomException ex){
        return ResponseEntity.badRequest().body(new ApiErrorResponse<>(new CustomError<>(ServerError.ERROR_OCCURRED,ex.getMessage())));
    }

}
