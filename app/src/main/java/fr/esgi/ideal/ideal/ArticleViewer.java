package fr.esgi.ideal.ideal;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class ArticleViewer extends AppCompatActivity {
    static int ObjectID = 1;
    ImageView ArticleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_viewer);

        ArticleView = findViewById(R.id.articleImage);
        ArticleView.setImageBitmap(MainActivity.dataModels.get(ObjectID).image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(MainActivity.dataModels.get(ObjectID).nom);
        toolbar.setTitleTextColor(0);
        //toolbar.setTitleTextAppearance(getApplicationContext(),R.style.AppTheme);
        setSupportActionBar(toolbar);

        //collapsingToolbarLayout.setTitleEnabled(false);


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
}
