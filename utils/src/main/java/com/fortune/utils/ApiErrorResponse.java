package com.fortune.utils;

public record ApiErrorResponse<T>(
        T error
) {
}
