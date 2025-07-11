package com.fortune.customer.request;

public record DataWrapper<T,Y> (
    T code,
    Y data
){}
