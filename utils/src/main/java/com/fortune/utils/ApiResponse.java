package com.fortune;

public class ApiResponse{

    public static <T,Y> ApiDataResponse<DataWrapper<T,Y>> data(T code, Y data){
        return new ApiDataResponse<>(
                new DataWrapper<>(code, data)
        );
    }

    public static <T,Y> ApiErrorResponse<DataWrapper<T,Y>> error(T code, Y data){
        return new ApiErrorResponse<>(
                new DataWrapper<>(code, data)
        );
    }
}