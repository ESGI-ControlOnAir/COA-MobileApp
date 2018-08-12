package fr.esgi.ideal.ideal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fr.esgi.ideal.ideal.api.Article;

public class ObjectList extends AppCompatActivity {
    static int ObjectID = 1;
    Button retour, star, like, unlike;
    TextView c1, c2, c3;
    TextView titre;
    TextView desc;
    TextView date;
    TextView prix;
    TextView content;
    public Article article;
    boolean fav = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_list);
        titre = (TextView) findViewById(R.id.titleart);
        desc = (TextView) findViewById(R.id.descobj);
        prix = (TextView) findViewById(R.id.priceobj);
        date = (TextView) findViewById(R.id.dateajoutobj);
        c1 = findViewById(R.id.like_count);
        c2 = findViewById(R.id.unlike_count);
        c3 = findViewById(R.id.vote_count);

        content = (TextView) findViewById(R.id.dateajoutobj);
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
                    star.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(android.R.drawable.star_big_on),null,null,null);
                    Toast.makeText(getApplicationContext(),
                            R.string.faved,
                            Toast.LENGTH_LONG).show();
                    star.setBackground(getResources().getDrawable(R.drawable.favorited));
                    fav = true;
                } else {
                    star.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(android.R.drawable.star_big_off),null,null,null);
                    star.setBackground(getResources().getDrawable(R.drawable.buttonchange));
                    fav = false;
                }
            }
        });

        like = (Button) findViewById(R.id.likebutton);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1.setText("1");
                c2.setText("0");
                c3.setText("1");
            }
        });

        unlike = (Button) findViewById(R.id.unlikebutton);
        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1.setText("0");
                c2.setText("1");
                c3.setText("1");
            }
        });

        //new ListReposTask().execute();

        titre.setText((CharSequence) MainActivity.dataModels.get(ObjectID).getName());
        desc.setText((CharSequence) MainActivity.dataModels.get(ObjectID).getDescription());
        prix.setText((CharSequence) MainActivity.dataModels.get(ObjectID).getPrix()+" €");
        //date.setText((CharSequence) MainActivity.dataModels.get(ObjectID).get);
    }

    public static void setObjectID(int ID_Article){
        ObjectID = ID_Article;
    }

    // Connexion au serveur et listage des objets dans /article
    /*class ListReposTask extends AsyncTask<Void, Void, Article> {

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
            } catch (IOException e) { Log.i("ddd","ERR "+e); }

            return article;
        }

        @Override
        protected void onPostExecute(Article art) {
            super.onPostExecute(art);

            //titre.setText(art.getName());
            //content.setText(article.);
        }
    }*/
}