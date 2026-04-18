package com.fortune;

public record CustomError<T,Y> (
        T code,
        Y data
){
}
