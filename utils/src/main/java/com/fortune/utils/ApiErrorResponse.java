package com.fortune;

public record ApiErrorResponse<T>(
        T error
) {
}
