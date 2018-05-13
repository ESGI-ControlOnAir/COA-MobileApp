package fr.esgi.ideal.ideal.oauth2.service;

import fr.esgi.ideal.ideal.oauth2.response.UserResponse;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by gokhan on 4/18/15.
 */
public interface IUserService {

    @GET("/user")
    void user(Callback<UserResponse> userResponseCallback);
}
