package fr.esgi.ideal.ideal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int TIME_OUT = 2000; // Vérification du serveur 2000ms
    private static int TIME_OUT_INTERNET = 1000; // Vérification de connexion internet 1000ms
    public static String URLServer = "10.33.1.60:8888"; // Variable globale de l'URL du serveur
    TextView connexion;
    ListView liste;
    Toolbar toolbar;
    RelativeLayout lay_loading;
    ProgressBar loader;
    List<Article> repoList = null;
    Button checkarticles;
    Button gosearch;
    public static String ACTIONMAIN = ""; // Tache de l'activité
    Handler Loadhandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des objets de la vue
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ProgressBar chargement = (ProgressBar) findViewById(R.id.progressBar);
        connexion = (TextView) findViewById(R.id.connexion);
        liste = (ListView) findViewById(R.id.liste);
        lay_loading = (RelativeLayout) findViewById(R.id.lay1);
        loader = (ProgressBar) findViewById(R.id.progressBar);
        gosearch = (Button) findViewById(R.id.gosearch);
        checkarticles = (Button) findViewById(R.id.button);
        checkarticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Adpage.ACTIVITYTOGO = ObjectList.class;
                Intent myIntent = new Intent(MainActivity.this, Adpage.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        setSupportActionBar(toolbar);
        checkarticles .setVisibility(View.INVISIBLE);
        gosearch.setVisibility(View.INVISIBLE);

        // Récupération de l'URL du serveur dans les paramètres si présent
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        URLServer = preferences.getString("URLServer", "10.33.1.60:8888");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    // L'activité a été dessiné - CF problème de fermeture trop tôt
    @Override
    protected void onStart() {
        super.onStart();
        Loadhandler = new Handler();
        Loadhandler.postDelayed(new Runnable() { // Affichage de la liste des objets vendu après connexion - TIMEOUT à des fins de test
            @Override
            public void run() {
                if(!isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(),
                            R.string.erreur_internet,
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    connexion.setText("Connexion en cours...\n["+URLServer+"]");
                    new ListReposTask().execute();
                }
            }
        }, TIME_OUT_INTERNET);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(MainActivity.this, ParamActivity.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }
        if (id == R.id.action_refresh) {
            finish();
            startActivity(getIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // SELECTION MENU GAUCHE
        int id = item.getItemId();

        if (id == R.id.nav_1) {

        } else if (id == R.id.nav_2) {

        } else if (id == R.id.nav_3) {

        } else if (id == R.id.nav_4) {

        } else if (id == R.id.nav_5) {

        } else if (id == R.id.nav_6) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Connexion au serveur et listage des objets dans /article
    class ListReposTask extends AsyncTask<Void, Void, List<Article>>{

        @Override
        protected List<Article> doInBackground(Void...params) {
            ApiService service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);

            try {
                repoList = service.listArticles().execute().body();
            } catch (IOException e) {}

            return repoList;
        }

        @Override
        protected void onPostExecute(List<Article> repos) {
            Log.i("jeej","PE");
            super.onPostExecute(repos);
            if(repos == null){ connexion.setText(R.string.erreur_reception); }
            else {
                // OK Connexion réussi
                lay_loading.setVisibility(View.INVISIBLE);
                connexion.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                gosearch.setVisibility(View.VISIBLE);
                checkarticles.setVisibility(View.VISIBLE);
                afficherArticles(repos);
            };
        }
    }

    void afficherArticles(List<Article> repos){
        // Create a List from String Array elements
        final List<String> str_list = new ArrayList();

        for(int z = 0;z<repos.size();z++)
        {
            str_list.add(repos.get(z).getName());
        }

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, str_list);

        // DataBind ListView with items from ArrayAdapter
        liste.setAdapter(arrayAdapter);
    }

    // Fonction générique de detection d'un accès internet existant
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
