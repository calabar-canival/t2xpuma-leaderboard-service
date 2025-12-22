package com.t2xafcon.leaderboard_service.config;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ApiResponseBody<T>{
    private boolean success;
    private T data;
    private List<String> errors;
    private int statusCode;
    private String message;

    public static <T> ApiResponseBody<T> success(T data) {
        ApiResponseBody<T> r = new ApiResponseBody<>();
        r.success = true;
        r.data = data;
        r.statusCode = 200;
        return r;
    }

    public static <T> ApiResponseBody<T> success(T data, String message) {
        ApiResponseBody<T> response = new ApiResponseBody<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    public static <T> ApiResponseBody<T> error(int code, List<String> errors) {
        ApiResponseBody<T> r = new ApiResponseBody<>();
        r.success = false;
        r.errors = errors;
        r.statusCode = code;
        return r;
    }
}
