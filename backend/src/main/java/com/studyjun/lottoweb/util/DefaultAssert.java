package com.studyjun.lottoweb.util;

import com.studyjun.lottoweb.exception.DefaultAuthenticationException;
import com.studyjun.lottoweb.exception.DefaultException;
import com.studyjun.lottoweb.exception.DefaultNullPointerException;
import com.studyjun.lottoweb.exception.ErrorCode;
import org.springframework.validation.Errors;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class DefaultAssert {

    public static void isTrue(boolean value){
        if(!value){
            throw new DefaultException(ErrorCode.INVALID_CHECK);
        }
    }

    public static void isTrue(boolean value, String message){
        if(!value){
            throw new DefaultException(ErrorCode.INVALID_CHECK, message);
        }
    }

    public static void isValidParameter(Errors errors){
        if(errors.hasErrors()){
            throw new InvalidParameterException(errors.toString());
        }
    }

    public static void isObjectNull(Object object){
        if(object == null){
            throw new DefaultNullPointerException(ErrorCode.INVALID_CHECK);
        }
    }

    public static void isListNull(List<Object> values){
        if(values == null || values.isEmpty()){
            throw new DefaultException(ErrorCode.INVALID_FILE_PATH);
        }
    }

    public static void isListNull(Object[] values){
        if(values == null || values.length == 0){
            throw new DefaultException(ErrorCode.INVALID_FILE_PATH);
        }
    }

    public static void isOptionalPresent(Optional<?> value){
        if(!value.isPresent()){
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }
    }

    public static void isAuthentication(String message){
        throw new DefaultAuthenticationException(message);
    }

    public static void isAuthentication(boolean value){
        if(!value){
            throw new DefaultAuthenticationException(ErrorCode.INVALID_AUTHENTICATION);
        }
    }
}