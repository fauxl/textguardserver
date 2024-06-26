package com.example.textguard2;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/logs")
    Call<ResponseBody> sendLogs(@Body Logs logs);
}
