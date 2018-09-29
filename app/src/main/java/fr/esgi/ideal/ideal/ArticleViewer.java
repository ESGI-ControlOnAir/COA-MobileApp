package fr.esgi.ideal.ideal;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ArticleViewer extends AppCompatActivity {
    static int ObjectID = 1;
    ImageView ArticleView;

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

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarviewer);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setTitle(MainActivity.dataModels.get(ObjectID).nom);
        toolbar.setTitleTextColor(0);
        //toolbar.setTitleTextAppearance(getApplicationContext(),R.style.AppTheme);
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
}
