package com.smartcity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecommendationCategoryNotFoundException extends RuntimeException {
    public RecommendationCategoryNotFoundException(String message) {
        super(message);
    }
}
