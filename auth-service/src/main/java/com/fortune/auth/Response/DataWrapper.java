package com.fortune.auth.Response;

public record DataWrapper<T,Y> (
    T code,
    Y data
){}
