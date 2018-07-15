package fr.esgi.ideal.ideal.api;

import java.util.List;

import fr.esgi.ideal.ideal.MainActivity;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kray on 09/04/2018.
 */

public interface ApiService {
    public static final String ENDPOINT = "http://"+ MainActivity.URLServer;

    @GET("/article")
    Call<List<Article>> listArticles();

    @GET("/article/{id}")
    Call<Article> getArticle(@Path("id") String article);

    @GET("/user/{id}")
    Call<User> getUser(@Path("id") String User);

    @GET("/user/me")
    Call<User> getUserMe();

    @GET("/user")
    Call<User> getUser();

    @GET("/partner")
    Call<Partner> getPartner();

    @GET("/partner/{id}")
    Call<Partner> getPartner(@Path("id") String Partner);

    @GET("/ad")
    Call<Ad> getAd();

    @GET("/ad/{id}")
    Call<Ad> getAd(@Path("id") String Ad);

    // OAUTH2
    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<AccessToken> getAccessToken(
            @Field("code") String code,
            @Field("grant_type") String grantType);
}
