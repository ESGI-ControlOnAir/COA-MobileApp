package fr.esgi.ideal.ideal.oauth2.service;

import fr.esgi.ideal.ideal.oauth2.response.ApiResponse;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by gokhan on 4/18/15.
 */
public interface IApiService {

    @GET("/")
    void getMessage(Callback<ApiResponse> responseCallback);

}
