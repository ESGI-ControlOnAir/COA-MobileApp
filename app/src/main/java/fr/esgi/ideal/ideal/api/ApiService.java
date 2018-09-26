package fr.esgi.ideal.ideal.api;

import java.util.List;
import java.util.jar.Attributes;

import fr.esgi.ideal.ideal.MainActivity;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kray on 09/04/2018.
 */

public interface ApiService {
    public static final String ENDPOINT = "http://"+ MainActivity.URLServer;

    @GET("/")
    Call<ResponseBody> getInit();

    @GET("/article")
    Call<List<Article>> listArticles();

    @GET("/article/{id}")
    Call<Article> getArticle(@Path("id") String article );

    @POST("/article")
    Call<ResponseBody> createArticle(@Header("Authorization") String code, @Body Article article );

    @GET("/user/{id}")
    Call<User> getUser(@Path("id") String User);

    @GET("/user/me")
    Call<User> getUserMe();

    @GET("/user")
    Call<User> getUser();

    @POST("/user")
    Call<User> createUser(@Header("Authorization") String code, User user);

    @GET("/partner")
    Call<Partner> getPartner();

    @GET("/partner/{id}")
    Call<Partner> getPartner(@Path("id") int ID);

    @GET("/ad")
    Call<Ad> getAd();

    @GET("/ad/{id}")
    Call<Ad> getAd(@Path("id") int ID);

    /* * * ImageServer * * */

    //@GET("/image/{id}/file")
    @GET("/public/{type}-{id}.jpg")
    Call<ResponseBody> retrieveImageData(@Path("type") String type, @Path("id") int ID);

    /*@POST("/upload")
    Call<ResponseBody> createImage(@Header("Authorization") String code, ImageAPI image);
    */
    @POST("/upload")
    Call<ResponseBody> postImage(@Part("file") MultipartBody.Part image, @Part("filename") String name);

    // OAUTH2
    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<AccessToken> getAccessToken(
            @Field("code") String code,
            @Field("grant_type") String grantType);
}
