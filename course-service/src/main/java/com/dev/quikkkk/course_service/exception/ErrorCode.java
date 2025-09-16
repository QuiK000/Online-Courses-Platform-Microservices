package com.dev.quikkkk.course_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with id %s", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "Entity not found with id %s", HttpStatus.NOT_FOUND),
    BAD_REQUEST("BAD_REQUEST", "Bad request", HttpStatus.BAD_REQUEST),
    FORBIDDEN("FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("UNAUTHORIZED", "Unauthorized", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_TOKEN("INVALID_JWT_TOKEN", "Invalid JWT token", HttpStatus.UNAUTHORIZED),
    COURSE_NOT_FOUND("COURSE_NOT_FOUND", "Course not found with id %s" , HttpStatus.NOT_FOUND ),
    LESSON_NOT_FOUND("LESSON_NOT_FOUND", "Lesson not found with id %s" , HttpStatus.NOT_FOUND ), ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
