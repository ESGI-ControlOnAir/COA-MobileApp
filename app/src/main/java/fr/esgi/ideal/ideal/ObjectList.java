package fr.esgi.ideal.ideal;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ObjectList extends AppCompatActivity {
    static int ObjectID = 1;
    Button retour, star, like;
    TextView titre;
    TextView content;
    public Article article;
    boolean fav = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_list);
        titre = (TextView) findViewById(R.id.titleart);
        content = (TextView) findViewById(R.id.contentart);
        retour = (Button) findViewById(R.id.button2);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        star = (Button) findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fav) {
                    star.setBackgroundResource(android.R.drawable.star_big_on);
                    fav = true;
                } else {
                    star.setBackgroundResource(android.R.drawable.star_big_off);
                    fav = false;
                }
            }
        });
        /*like = (Button) findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(like.getText().toString().contains("J'aime")){
                    like.setText("Je n\'aime plus ce produit");
                } else { like.setText("J\'aime ce produit"); }
            }
        });*/

        new ListReposTask().execute();
    }

    public static void setObjectID(int ID_Article){
        ObjectID = ID_Article;
    }

    // Connexion au serveur et listage des objets dans /article
    class ListReposTask extends AsyncTask<Void, Void, Article> {

        @Override
        protected Article doInBackground(Void...params) {
            ApiService service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);

            try {
                article = service.getArticle(String.valueOf(ObjectID)).execute().body();
            } catch (IOException e) {}

            return article;
        }

        @Override
        protected void onPostExecute(Article art) {
            super.onPostExecute(art);
            titre.setText(article.getName());
            //content.setText(article.);
        }
    }
}