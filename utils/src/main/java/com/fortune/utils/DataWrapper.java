package com.fortune.utils;

public record DataWrapper<T,Y>(
        T code,
        Y data
) {
}
