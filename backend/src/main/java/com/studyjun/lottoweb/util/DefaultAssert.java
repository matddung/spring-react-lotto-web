package com.studyjun.lottoweb.util;

import com.studyjun.lottoweb.exception.*;
import org.springframework.validation.Errors;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class DefaultAssert {
    public static void isTrue(boolean value) {
        if (!value) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public static void isTrue(boolean value, String message) {
        if (!value) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, message);
        }
    }

    public static void isValidParameter(Errors errors) {
        if (errors.hasErrors()) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, errors.toString());
        }
    }

    public static void isObjectNull(Object object) {
        if (object == null) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public static void isListNull(List<Object> values) {
        if (values == null || values.isEmpty()) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public static void isListNull(Object[] values) {
        if (values == null || values.length == 0) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public static void isOptionalPresent(Optional<?> value) {
        if (value.isEmpty()) {
            throw new BusinessException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    public static void isAuthentication(String message) {
        throw new BusinessException(AuthErrorCode.UNAUTHORIZED, message);
    }

    public static void isAuthentication(boolean value) {
        if (!value) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }
    }

    public static void isForbidden(boolean value, String message) {
        if (!value) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN, message);
        }
    }
}