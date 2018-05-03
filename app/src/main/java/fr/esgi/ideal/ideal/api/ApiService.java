package fr.esgi.ideal.ideal.api;

import java.util.List;

import fr.esgi.ideal.ideal.MainActivity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kray on 09/04/2018.
 */

public interface ApiService {
    public static final String ENDPOINT = "http://"+ MainActivity.URLServer;

    //TODO @GET("/articles")
    @GET("/article")
    Call<List<Article>> listArticles();

    @GET("/article/{id}")
    Call<Article> getArticle(@Path("id") String article);

    /*@POST("/login")
    Call<User> basicLogin();*/
}
