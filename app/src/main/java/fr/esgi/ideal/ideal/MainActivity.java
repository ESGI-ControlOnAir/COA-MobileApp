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
import android.os.Message;
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
import android.view.KeyEvent;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int TIME_OUT_INTERNET = 0; // Vérification de connexion internet 100ms
    public static String URLServer = "10.33.1.60:8888"; // Variable globale de l'URL du serveur
    public static String AccessToken = null;
    public static boolean addisplayed = false;
    public static boolean ACCESS = false;
    public static boolean changedlang = false;
    public static boolean acceptedRGDP = false;
    protected ImageView bg;
    protected int i=0, j=0;
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
    public static ArrayList<objetEnVente> dataModels;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des objets de la vue
        bg = (ImageView) findViewById(R.id.mainbg);
        bg.setScaleType(ImageView.ScaleType.CENTER);
        bg.setPadding(0,0,0,0);
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
                Intent myIntent = new Intent(MainActivity.this, ObjectList.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        lay_loading = (RelativeLayout) findViewById(R.id.lay1);
        lay2 = (RelativeLayout) findViewById(R.id.lay2);
        loader = (ProgressBar) findViewById(R.id.progressBar);
        // RECHERCHE
        search = (ConstraintLayout) findViewById(R.id.search);
        searchword = (TextView) findViewById(R.id.searchbar);
        searchword.setOnKeyListener(new View.OnKeyListener() { // VALIDATION PAR TOUCHE ENTREE
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // ENTER = RECHERCHER
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    new ListReposTask().execute();
                    return true;
                }
                return false;
            }
        });

        // Bouton "GO" de recherche
        gosearch = (Button) findViewById(R.id.gosearch);
        gosearch.setOnClickListener(new View.OnClickListener() { // VALIDATION PAR LE BOUTON "GO"
            @Override
            public void onClick(View v) {
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                Intent myIntent = new Intent(MainActivity.this, Adpage.class);
                MainActivity.this.startActivity(myIntent);
                new ListReposTask().execute();
            }
        });

        // BOUTON "VOIR LES ARTICLES"
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

        handler.sendEmptyMessageDelayed(1, 100);
    }
    public void clickButton(){
        checkarticles.setBackgroundResource(R.drawable.greengradiantpushed);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            i++;
            Log.i("tag","HANDLER "+ Integer.toString(i));

            if(i<70) {
                bg.setImageResource(R.drawable.intro1);
                bg.setScaleType(ImageView.ScaleType.MATRIX);
                bg.setPadding(i * 6 +300, 0, i * 6, 0);
                bg.invalidate(); // REDRAW
            }
            if(i>60 && i<70) {
                j++;
                bg.setImageAlpha(250-(25*j));
            }
            if(i>70 && i<80) {
                j--;
                bg.setImageAlpha(250-(25*j));
            }
            if(i>70 && i<160) {
                bg.setImageResource(R.drawable.intro2);
                bg.setScaleType(ImageView.ScaleType.MATRIX);
                bg.setPadding(i * -6 +700, 0, i * -6, 0);
                bg.invalidate(); // REDRAW
            }
            if(i>150 && i<160) {
                j++;
                bg.setImageAlpha(250-(25*j));
            }
            if(i>160 && i<170) {
                j--;
                bg.setImageAlpha(250-(25*j));
            }
            if(i>160 && i<240) {
                bg.setImageResource(R.drawable.intro3);
                bg.setScaleType(ImageView.ScaleType.MATRIX);
                bg.setPadding(i * -6 + 960, 0, /*i * -6 + 26*/0, 0);
                bg.invalidate(); // REDRAW
            }
            if(i==241) {
                j = 0; i = 0;
            }

            handler.sendEmptyMessageDelayed(1, 30);
        }
    };

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
    protected void onRestart() {
        super.onRestart();
        if(changedlang){
            recreate();
            changedlang = false;
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("jj","on post resume");
        isauth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("jj","on resume");
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

        } else if (id == R.id.nav_5) { // Ajouter un obj : forward dans loginactivity
                LoginActivity.classtogo = 5;
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_6) { // Mon compte : forward dans loginactivity
                LoginActivity.classtogo = 6;
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);

        } else if (id == R.id.nav_7) { // Mes favs : forward dans loginactivity
                LoginActivity.classtogo = 7;
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
        } else if (id == R.id.nav_8) { // Logout : forward dans loginactivity
            LoginActivity.classtogo = 8;
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

        dataModels = new ArrayList<>();

        for (int z = 0; z < repos.size(); z++) {
            if( repos.get(z).getName().toLowerCase().contains(searchword.getText().toString().toLowerCase()) ) {
                double Price = repos.get(z).getPrice();
                Random r = new Random();
                double randomValue = 10 + (30 - 10) * r.nextDouble();
                if (Price == 0) {
                    Price = randomValue;
                }
                dataModels.add(new objetEnVente(repos.get(z).getName(), repos.get(z).getDescription(), String.format("%.2f",Price), Integer.toString(repos.get(z).getLike()), ""));
            }
        }

        if(repos.size()==0){
            Toast.makeText(getApplicationContext(),
                    R.string.no_res,
                    Toast.LENGTH_LONG).show();
        }

        if(sortMode == 1){
            //Collections.sort(objlist,String.CASE_INSENSITIVE_ORDER);
        }

        // création de l'adapteur de la liste
        adapter = new CustomAdapter(dataModels, getApplicationContext());

        if(adapter != null) liste.setAdapter(adapter);
    }

    // Fonction générique de detection d'un accès internet existant
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
