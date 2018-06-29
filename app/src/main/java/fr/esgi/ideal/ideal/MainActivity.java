package fr.esgi.ideal.ideal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int TIME_OUT_INTERNET = 1000; // Vérification de connexion internet 1000ms
    public static String URLServer = "10.33.1.60:8888"; // Variable globale de l'URL du serveur
    public static String AccessToken = null;
    public static boolean addisplayed = false;
    public static boolean ACCESS = false;
    TextView connexion;
    ListView liste;
    Toolbar toolbar;
    RelativeLayout lay_loading, lay2;
    ProgressBar loader;
    List<Article> repoList = null;
    Button checkarticles, gosearch;
    ConstraintLayout search;
    TextView searchword;
    public static String ACTIONMAIN = ""; // Tache de l'activité
    Handler Loadhandler = null;
    int sortMode = 0;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des objets de la vue
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        final ProgressBar chargement = (ProgressBar) findViewById(R.id.progressBar);
        connexion = (TextView) findViewById(R.id.connexion);
        liste = (ListView) findViewById(R.id.liste);
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                ObjectList.setObjectID((position+1));
                Toast.makeText(getApplicationContext(), "POS " + position, Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MainActivity.this, ObjectList.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        lay_loading = (RelativeLayout) findViewById(R.id.lay1);
        lay2 = (RelativeLayout) findViewById(R.id.lay2);
        loader = (ProgressBar) findViewById(R.id.progressBar);
        search = (ConstraintLayout) findViewById(R.id.search);
        searchword = (TextView) findViewById(R.id.searchbar);
        gosearch = (Button) findViewById(R.id.gosearch);
        gosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ListReposTask().execute();
            }
        });
        checkarticles = (Button) findViewById(R.id.button);
        checkarticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                Intent myIntent = new Intent(MainActivity.this, Adpage.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        setSupportActionBar(toolbar);

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
    public void clickButton(){
        checkarticles.setBackgroundResource(R.drawable.greengradiantpushed);
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
                    connexion.setText(getText(R.string.connecting)+"\n["+URLServer+"]");
                    new ListReposTask().execute();
                }
            }
        }, TIME_OUT_INTERNET);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        isauth();
    }

    private void isauth() {
        if(AccessToken != null) {
            ImageView auth = (ImageView) findViewById(R.id.etatauth);
            auth.setImageResource(android.R.drawable.presence_online);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) { // Menu paramètre droite
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Selection menu paramètre droite
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
    public boolean onNavigationItemSelected(MenuItem item) { // SELECTION MENU GAUCHE
        int id = item.getItemId();

        if( !ACCESS ) {
            Toast.makeText(getApplicationContext(),
                    R.string.access_err,
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (id == R.id.nav_1) { // Tri alpha
            sortAlphabetic();
            liste.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.INVISIBLE);
            Intent myIntent = new Intent(MainActivity.this, Adpage.class);
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_2) { // Tri prix
            liste.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.INVISIBLE);
            Intent myIntent = new Intent(MainActivity.this, Adpage.class);
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_3) { // Tri Note
            liste.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.INVISIBLE);
            Intent myIntent = new Intent(MainActivity.this, Adpage.class);
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_4) { // Tri Date
            liste.setVisibility(View.VISIBLE);
            lay2.setVisibility(View.INVISIBLE);
            Intent myIntent = new Intent(MainActivity.this, Adpage.class);
            MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_5) { // Ajouter un obj
                LoginActivity.classtogo = 5;
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_6) { // Mon compte
                LoginActivity.classtogo = 6;
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_7) { // Mes favs
                LoginActivity.classtogo = 7;
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sortAlphabetic(){
        new ListReposTask().execute();
        sortMode = 1;
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
            super.onPostExecute(repos);
            if(repos == null){ connexion.setText(R.string.erreur_reception); }
            else {
                // OK Connexion réussi
                ImageView co = (ImageView) findViewById(R.id.etatco);
                co.setImageResource(android.R.drawable.presence_online);
                lay_loading.setVisibility(View.INVISIBLE);
                connexion.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
                checkarticles.setEnabled(true);
                ACCESS = true;
                afficherArticles(repos);
            };
        }
    }

    void afficherArticles(List<Article> repos){

        final List<String> str_list = new ArrayList();

        for (int z = 0; z < repos.size(); z++) {
            if( repos.get(z).getName().toLowerCase().contains(searchword.getText().toString().toLowerCase()) )
                str_list.add(repos.get(z).getName());
        }

        if(sortMode == 1){
            Collections.sort(str_list,String.CASE_INSENSITIVE_ORDER);
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
