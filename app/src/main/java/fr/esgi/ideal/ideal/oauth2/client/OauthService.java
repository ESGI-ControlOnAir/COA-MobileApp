package fr.esgi.ideal.ideal.oauth2.client;

import fr.esgi.ideal.ideal.oauth2.constant.OauthConstant;
import fr.esgi.ideal.ideal.oauth2.service.IOauthService;
import retrofit.RestAdapter;

/**
 * Created by gokhan on 4/8/15.
 */
public class OauthService {

    private IOauthService _oauthService;


    public IOauthService getAccessToken() {
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(OauthConstant.AUTHENTICATION_SERVER_URL)
                .build();
        _oauthService = restAdapter.create(IOauthService.class);


        return _oauthService;
    }
}