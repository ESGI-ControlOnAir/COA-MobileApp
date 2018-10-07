package fr.esgi.ideal.ideal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import fr.esgi.ideal.ideal.api.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ArticleViewer extends AppCompatActivity {
    static int ObjectID = 1;
    ImageView ArticleView;
    boolean liked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_viewer);

        ArticleView = findViewById(R.id.articleImage);
        ArticleView.setImageBitmap(MainActivity.dataModels.get(ObjectID).image);

        // Description
        final RelativeLayout descextend = (RelativeLayout) findViewById(R.id.articledescriptionextend);
        final Button descextendicon = (Button) findViewById(R.id.extenddesc);
        final TextView descedit = (TextView) findViewById(R.id.articledescription);
        descedit.setText(MainActivity.dataModels.get(ObjectID).description);
        descextend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( descedit.getVisibility() == View.GONE ){
                    descextendicon.setBackgroundResource(android.R.drawable.arrow_down_float);
                    descedit.setVisibility(View.VISIBLE);
                } else {
                    descextendicon.setBackgroundResource(android.R.drawable.arrow_up_float);
                    descedit.setVisibility(View.GONE);
                }
            }
        });

        // Prix
        final RelativeLayout prixextend = (RelativeLayout) findViewById(R.id.articleprixextend);
        final Button prixextendicon = (Button) findViewById(R.id.extendprix);
        final TextView prixedit = (TextView) findViewById(R.id.articleprix);
        final LinearLayout prixlay = (LinearLayout) findViewById(R.id.articleprixlay);
        prixedit.setText(MainActivity.dataModels.get(ObjectID).prix);
        prixextend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( prixlay.getVisibility() == View.GONE ){
                    prixextendicon.setBackgroundResource(android.R.drawable.arrow_down_float);
                    prixlay.setVisibility(View.VISIBLE);
                } else {
                    prixextendicon.setBackgroundResource(android.R.drawable.arrow_up_float);
                    prixlay.setVisibility(View.GONE);
                }
            }
        });

        // Likes
        final RelativeLayout likesextend = (RelativeLayout) findViewById(R.id.articlelikesextend);
        final Button likesextendicon = (Button) findViewById(R.id.extendlikes);
        final LinearLayout likesedit = findViewById(R.id.likeslay);
        final Button like = findViewById(R.id.like);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked = true;
                new Like().execute();
            }
        });
        final Button unlike = findViewById(R.id.unlike);
        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liked = false;
                new Like().execute();
            }
        });

        likesextend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( likesedit.getVisibility() == View.GONE ){
                    likesextendicon.setBackgroundResource(android.R.drawable.arrow_down_float);
                    likesedit.setVisibility(View.VISIBLE);
                } else {
                    likesextendicon.setBackgroundResource(android.R.drawable.arrow_up_float);
                    likesedit.setVisibility(View.GONE);
                }
            }
        });

        final Button back = (Button) findViewById(R.id.backarticleview);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // fav
        final RelativeLayout favextend = (RelativeLayout) findViewById(R.id.articlefavextend);
        final Button favextendicon = (Button) findViewById(R.id.extendfav);
        final TextView favedit = (TextView) findViewById(R.id.articlefav);
        final Button favadd = (Button) findViewById(R.id.favadd);
        favextend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( favedit.getVisibility() == View.GONE ){
                    favextendicon.setBackgroundResource(android.R.drawable.arrow_down_float);
                    favedit.setVisibility(View.VISIBLE);
                    favadd.setVisibility(View.VISIBLE);
                } else {
                    favextendicon.setBackgroundResource(android.R.drawable.arrow_up_float);
                    favedit.setVisibility(View.GONE);
                    favadd.setVisibility(View.GONE);
                }
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarviewer);


        toolbar.setTitle(MainActivity.dataModels.get(ObjectID).nom);
        toolbar.setTitleTextColor(0);
        toolbar.setTitleTextAppearance(getApplicationContext(),R.style.AppTheme);
        setSupportActionBar(toolbar);

        //collapsingToolbarLayout.setTitleEnabled(false);
        final Toolbar tooolbar = (Toolbar) findViewById(R.id.toolbarviewer);

        final NestedScrollView mScrollView = (NestedScrollView) findViewById(R.id.scrollarticle);
        mScrollView.post(new Runnable(){

            @Override
            public void run() {
                ViewTreeObserver observer = mScrollView.getViewTreeObserver();
                observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener(){

                    @Override
                    public void onScrollChanged() {
                        int scrollX = mScrollView.getScrollX();
                        int scrollY = mScrollView.getScrollY();
                        Log.i("err","POS "+scrollY+" ; "+scrollX);
                        if(scrollY>0) tooolbar.setVisibility(View.GONE);
                            else tooolbar.setVisibility(View.VISIBLE);
                    }});
            }});

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public static void setObjectID(int ID_Article){
        ObjectID = ID_Article;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    class Like extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void...params) {
            ApiService service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
            Call<ResponseBody> call = null;
            if(liked) service.setLike("true",ObjectID);
            else service.setLike("false",ObjectID);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    String lliked = "Article liké avec succès";
                    if(!liked)lliked = "Article unliké avec succès";
                    Toast.makeText(getBaseContext(),
                            lliked,
                            Toast.LENGTH_LONG).show();
                    recreate();
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i("err","shitlike");
                    t.printStackTrace();
                }
            });
            return 1;
        }
    }
}
