package com.fortune;

public record DataWrapper<T,Y>(
        T code,
        Y data
) {
}
