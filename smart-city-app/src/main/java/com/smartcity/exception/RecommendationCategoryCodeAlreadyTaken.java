package com.smartcity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecommendationCategoryCodeAlreadyTaken extends RuntimeException {
    public RecommendationCategoryCodeAlreadyTaken(String message) {
        super(message);
    }
}
