package com.fortune.utils;

public record CustomError<T,Y> (
        T code,
        Y data
){
}
