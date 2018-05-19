package fr.esgi.ideal.ideal.oauth2.service;

import fr.esgi.ideal.ideal.oauth2.request.AccessTokenRequest;
import fr.esgi.ideal.ideal.oauth2.response.AccessTokenResponse;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;


public interface IOauthService {

    @POST("/oauth2/token")
    void getAccessToken(@Body AccessTokenRequest accessTokenRequest,
                        Callback<AccessTokenResponse> responseCallback);


}
