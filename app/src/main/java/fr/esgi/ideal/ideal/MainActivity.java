package fr.esgi.ideal.ideal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.widget.Toast;

import static java.lang.StrictMath.toIntExact;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int TIME_OUT_INTERNET = 0; // Vérification de connexion internet 100ms
    public static String URLServer = "10.33.1.60:18080"; // Variable globale de l'URL du serveur Tristan port 18080
    public static String URLServerImage = "10.33.1.60:8000"; // Images port 8000
    public static String AccessToken = null;
    public static boolean addisplayed = false;
    public static boolean ACCESS = false;
    public static boolean changedlang = false;
    public static boolean acceptedRGDP = false;
    public static String email = "";
    NavigationView navigationView;
    protected ImageView bg;
    protected int i=0, j=0, h=0;
    ImageView Fleche1, Fleche2;
    TextView connexion, fleche1txt, fleche2txt, user;
    ListView liste;
    Toolbar toolbar;
    RelativeLayout lay_loading, lay2;
    ProgressBar loader;
    List<Article> repoList = null;
    Boolean showall = false;
    Button checkarticles, gosearch, connexiontop, comptetop, regbutton, searchbarbut;
    ConstraintLayout search;
    TextView searchword;
    public static String ACTIONMAIN = ""; // Tache de l'activité
    Handler Loadhandler = null;

    View view;
    Boolean stophandler = false;
    AsyncTask tache = null;
    public static ArrayList<objetEnVente> dataModels;
    private static CustomAdapter adapter;
    ApiService service = null;
    View footerView = null;
    TranslateAnimation animate = null;
    RelativeLayout morearticle = null;
    LinearLayout moreatic = null;
    ProgressBar pb = null;

    int sortMode = 0;
    Boolean descmode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Initialisation des objets de la vue
        bg = (ImageView) findViewById(R.id.mainbg);
        bg.setScaleType(ImageView.ScaleType.CENTER);
        bg.setPadding(0,0,0,0);
        Fleche1 = (ImageView) findViewById(R.id.checkit1);
        fleche1txt = (TextView) findViewById(R.id.fleche1txt);
        Fleche2 = (ImageView) findViewById(R.id.checkit2);
        fleche2txt = (TextView) findViewById(R.id.fleche2txt);
        toolbar = (Toolbar) findViewById(R.id.toolbarviewer);
        final ProgressBar chargement = (ProgressBar) findViewById(R.id.progressBar);
        connexion = (TextView) findViewById(R.id.connexion);
        liste = (ListView) findViewById(R.id.liste);
        HorizontalScrollView scrollbar = (HorizontalScrollView) findViewById(R.id.scrollbar);
        scrollbar.setHorizontalScrollBarEnabled(false);
        liste.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                /*ObjectList.setObjectID((position));*/
                ArticleViewer.setObjectID((position));
                Intent myIntent = new Intent(MainActivity.this, /*ObjectList.class*/ ArticleViewer.class);
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
                    stophandler = true;
                    showall = false;
                    liste.setVisibility(View.VISIBLE);
                    lay2.setVisibility(View.INVISIBLE);
                    tache = new ListReposTask().execute();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    Fleche1.setVisibility(View.GONE);
                    Fleche2.setVisibility(View.GONE);
                    fleche1txt.setVisibility(View.GONE);
                    fleche2txt.setVisibility(View.GONE);
                    //return true;
                }
                return false;
            }
        });

        footerView = getLayoutInflater().inflate(R.layout.bottomlist, null);
        morearticle = footerView.findViewById(R.id.morearticle);
        moreatic = footerView.findViewById(R.id.linearmorearticle);
        pb = footerView.findViewById(R.id.progressBarmorearticle);
        pb.setVisibility(View.GONE);
        morearticle.setVisibility(View.VISIBLE);
        morearticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stophandler = true;
                showall = true;
                pb.setVisibility(View.VISIBLE);
                moreatic.setVisibility(View.GONE);
                tache = new ListReposTask().execute();
            }
        });

        // BOUTONS TRIS
        final Button butt1 = (Button) findViewById(R.id.butt1); // NOUVEAU DEFAULT
        final Button butt2 = (Button) findViewById(R.id.butt2);
        final Button butt3 = (Button) findViewById(R.id.butt3);
        final Button butt4 = (Button) findViewById(R.id.butt4);
        final Button butt5 = (Button) findViewById(R.id.butt5);
        final Button butt1_ = (Button) findViewById(R.id.butt1_); // NOUVEAU DEFAULT
        final Button butt2_ = (Button) findViewById(R.id.butt2_);
        final Button butt3_ = (Button) findViewById(R.id.butt3_);
        final Button butt4_ = (Button) findViewById(R.id.butt4_);
        final Button butt5_ = (Button) findViewById(R.id.butt5_);

        butt1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                butt1.setVisibility(View.GONE);
                butt1_.setVisibility(View.VISIBLE);

                butt2.setVisibility(View.VISIBLE);
                butt2_.setVisibility(View.GONE);
                butt3.setVisibility(View.VISIBLE);
                butt3_.setVisibility(View.GONE);
                butt4.setVisibility(View.VISIBLE);
                butt4_.setVisibility(View.GONE);
                butt5.setVisibility(View.VISIBLE);
                butt5_.setVisibility(View.GONE);
                sortMode = 0;

                stophandler = true;
                showall = false;
                tache = new ListReposTask().execute();
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.INVISIBLE);
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                fleche2txt.setVisibility(View.INVISIBLE);
            }
        });

        butt2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                butt2.setVisibility(View.GONE);
                butt2_.setVisibility(View.VISIBLE);

                butt1.setVisibility(View.VISIBLE);
                butt1_.setVisibility(View.GONE);
                butt3.setVisibility(View.VISIBLE);
                butt3_.setVisibility(View.GONE);
                butt4.setVisibility(View.VISIBLE);
                butt4_.setVisibility(View.GONE);
                butt5.setVisibility(View.VISIBLE);
                butt5_.setVisibility(View.GONE);
                sortMode = 1;

                stophandler = true;
                showall = false;
                tache = new ListReposTask().execute();
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.INVISIBLE);
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                fleche2txt.setVisibility(View.INVISIBLE);
            }
        });

        butt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                butt3.setVisibility(View.GONE);
                butt3_.setVisibility(View.VISIBLE);

                butt1.setVisibility(View.VISIBLE);
                butt1_.setVisibility(View.GONE);
                butt2.setVisibility(View.VISIBLE);
                butt2_.setVisibility(View.GONE);
                butt4.setVisibility(View.VISIBLE);
                butt4_.setVisibility(View.GONE);
                butt5.setVisibility(View.VISIBLE);
                butt5_.setVisibility(View.GONE);
                sortMode = 2;

                stophandler = true;
                showall = false;
                tache = new ListReposTask().execute();
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.INVISIBLE);
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                fleche2txt.setVisibility(View.INVISIBLE);
            }
        });

        butt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                butt4.setVisibility(View.GONE);
                butt4_.setVisibility(View.VISIBLE);

                butt1.setVisibility(View.VISIBLE);
                butt1_.setVisibility(View.GONE);
                butt3.setVisibility(View.VISIBLE);
                butt3_.setVisibility(View.GONE);
                butt2.setVisibility(View.VISIBLE);
                butt2_.setVisibility(View.GONE);
                butt5.setVisibility(View.VISIBLE);
                butt5_.setVisibility(View.GONE);
                sortMode = 3;

                stophandler = true;
                showall = false;
                tache = new ListReposTask().execute();
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.INVISIBLE);
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                fleche2txt.setVisibility(View.INVISIBLE);
            }
        });

        butt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                butt5.setVisibility(View.GONE);
                butt5_.setVisibility(View.VISIBLE);

                butt1.setVisibility(View.VISIBLE);
                butt1_.setVisibility(View.GONE);
                butt3.setVisibility(View.VISIBLE);
                butt3_.setVisibility(View.GONE);
                butt4.setVisibility(View.VISIBLE);
                butt4_.setVisibility(View.GONE);
                butt2.setVisibility(View.VISIBLE);
                butt2_.setVisibility(View.GONE);
                sortMode = 4;

                stophandler = true;
                showall = false;
                tache = new ListReposTask().execute();
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.INVISIBLE);
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                fleche2txt.setVisibility(View.INVISIBLE);
            }
        });

        // BOUTON SORT ASC / DESC
        final Button notifbut = (Button) findViewById(R.id.notifbarbut);
        notifbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(descmode){
                    notifbut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_sort_by_size,0,0,0);
                    descmode = false;
                    if(liste.getVisibility() == View.VISIBLE){
                        stophandler = true;
                        showall = false;
                        tache = new ListReposTask().execute();
                    }
                } else {
                    notifbut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_sort_by_size_rev,0,0,0);
                    descmode = true;
                    if(liste.getVisibility() == View.VISIBLE){
                        stophandler = true;
                        showall = false;
                        tache = new ListReposTask().execute();
                    }
                }
            }
        });

        // BOUTON LOUPE CHERCHER
        searchbarbut = (Button) findViewById(R.id.searchbarbut);
        searchbarbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(search.getVisibility() == View.VISIBLE){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 1);

                    animate = new TranslateAnimation(0,search.getWidth(),0,0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    search.startAnimation(animate);
                    search.setVisibility(View.INVISIBLE);
                    Loadhandler = new Handler();
                    Loadhandler.postDelayed(new Runnable() { // Affichage de la liste des objets vendu après connexion - TIMEOUT à des fins de test
                        @Override
                        public void run() {
                            search.setVisibility(View.GONE);
                        }
                    }, 500);

                    searchbarbut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_category_defaul2,0,0,0);
                }else{
                    search.setVisibility(View.VISIBLE);
                    animate = new TranslateAnimation(search.getWidth(),0,0,0);
                    animate.setDuration(250);
                    animate.setFillAfter(true);
                    search.startAnimation(animate);
                    searchword.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    searchbarbut.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_category_default,0,0,0);
                }
            }
        });

        // Bouton "GO" de recherche
        gosearch = (Button) findViewById(R.id.gosearch);
        gosearch.setOnClickListener(new View.OnClickListener() { // VALIDATION PAR LE BOUTON "GO"
            @Override
            public void onClick(View v) {
                stophandler = true;
                showall = false;
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                /*Intent myIntent = new Intent(MainActivity.this, Adpage.class);
                MainActivity.this.startActivity(myIntent);*/
                new ListReposTask().execute();
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.GONE);
                fleche2txt.setVisibility(View.GONE);
            }
        });

        // BOUTON "VOIR LES ARTICLES"
        checkarticles = (Button) findViewById(R.id.button);
        checkarticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butt1.setVisibility(View.GONE);
                butt1_.setVisibility(View.VISIBLE);

                butt2.setVisibility(View.VISIBLE);
                butt2_.setVisibility(View.GONE);
                butt3.setVisibility(View.VISIBLE);
                butt3_.setVisibility(View.GONE);
                butt4.setVisibility(View.VISIBLE);
                butt4_.setVisibility(View.GONE);
                butt5.setVisibility(View.VISIBLE);
                butt5_.setVisibility(View.GONE);
                sortMode = 0;
                Intent myIntent = new Intent(MainActivity.this, Adpage.class);
                stophandler = true;
                showall = false;
                tache = new ListReposTask().execute();
                MainActivity.this.startActivity(myIntent);
                Fleche1.setVisibility(View.GONE);
                Fleche2.setVisibility(View.GONE);
                fleche1txt.setVisibility(View.INVISIBLE);
                liste.setVisibility(View.VISIBLE);
                lay2.setVisibility(View.INVISIBLE);
                fleche2txt.setVisibility(View.INVISIBLE);
            }
        });
        setSupportActionBar(toolbar);

        // BOUTON "CONNEXION"
        /*connexiontop = (Button) findViewById(R.id.barbut1);
        connexiontop.setOnClickListener(new View.OnClickListener() { // VALIDATION PAR LE BOUTON "GO"
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });*/

        // BOUTON "MON COMPTE"
        /*comptetop = (Button) findViewById(R.id.barbut2);
        comptetop.setOnClickListener(new View.OnClickListener() { // VALIDATION PAR LE BOUTON "GO"
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AccountActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });*/

        /*CreateNotificationActivity notif = new CreateNotificationActivity();
        notif.createNotification(this.view);*/

        // Récupération de l'URL du serveur dans les paramètres si présent
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        URLServer = preferences.getString("URLServer", "10.33.1.60:18080");
        URLServerImage = preferences.getString("URLServerImage", "10.33.1.60:8000");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = (TextView) findViewById(R.id.utilisateur);
        regbutton = (Button) findViewById(R.id.registerbutton);
        regbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        handler.sendEmptyMessageDelayed(1, 150);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            i++;
            h++;

            // Fleche bounding
            if(h<=8) {
                Fleche1.setPadding(0,-h*4,0,0);
                Fleche2.setPadding(0,h*4,0,0);
            }
            if(h>8 && h<16) {
                Fleche1.setPadding(0,-40+(h*4),0,0);
                Fleche2.setPadding(0,40-(h*4),0,0);
            }
            if(h == 16) {
                h = 0;
            }

            // Fond BG
            if(i<70) {
                bg.setImageResource(R.drawable.intro1);
                bg.setScaleType(ImageView.ScaleType.MATRIX);
                bg.setPadding(i * 6 +300, 0, i * 6, 0);
                bg.invalidate(); // REDRAW
            }
            if(i>60 && i<70) {
                i++;
                j++;
                bg.setImageAlpha(250-(50*j));
            }
            if(i>70 && i<80) {
                i++;
                j--;
                bg.setImageAlpha(250-(50*j));
            }
            if(i>70 && i<160) {
                bg.setImageResource(R.drawable.intro2);
                bg.setScaleType(ImageView.ScaleType.MATRIX);
                bg.setPadding(i * -6 +700, 0, i * -6, 0);
                bg.invalidate(); // REDRAW
            }
            if(i>150 && i<160) {
                i++;
                j++;
                bg.setImageAlpha(250-(50*j));
            }
            if(i>160 && i<170) {
                i++;
                j--;
                bg.setImageAlpha(250-(50*j));
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

            if(!stophandler)handler.sendEmptyMessageDelayed(1, 40);
        }
    };

    // L'activité a été dessiné - CF problème de fermeture trop tôt
    @Override
    protected void onStart() {
        super.onStart();

        //search.setVisibility(View.GONE);

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
                    tache = new TryConnect().execute();
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
        isauth();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Menu menu = navigationView.getMenu();
        MenuItem conmenu = menu.findItem(R.id.connectedmenu);
        MenuItem unconmenu = menu.findItem(R.id.unconnectedmenu);
        /*Button button1 = findViewById(R.id.barbut1);
        Button button2 = findViewById(R.id.barbut2);
        Button button3 = findViewById(R.id.barbut3);*/

        if (AccessToken == null )
        {
            conmenu.setVisible(false);
            unconmenu.setVisible(true);
            //button1.setVisibility(View.VISIBLE);
            //button2.setVisibility(GONE);
            //button3.setVisibility(View.GONE);
            if(user!=null)user.setText(R.string.invit);
            //regbutton.setVisibility(View.VISIBLE);
        }
        else
        {
            unconmenu.setVisible(false);
            conmenu.setVisible(true);
            //button1.setVisibility(GONE);
            //button2.setVisibility(View.VISIBLE);
            //button3.setVisibility(View.VISIBLE);
            if(user!=null)user.setText(email);
            regbutton.setVisibility(View.GONE);
            fleche1txt.setVisibility(View.GONE);
            fleche2txt.setVisibility(View.GONE);
            Fleche1.setVisibility(View.GONE);
            Fleche2.setVisibility(View.GONE);
        }

        stophandler = false;
        handler.sendEmptyMessageDelayed(1, 150);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) { // Menu gauche
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuItem conmenu = menu.findItem(R.id.connectedmenu);
        MenuItem unconmenu = menu.findItem(R.id.unconnectedmenu);
        /*Button button1 = findViewById(R.id.barbut1);
        Button button2 = findViewById(R.id.barbut2);
        Button button3 = findViewById(R.id.barbut3);*/

        if (AccessToken != null )
        {
            conmenu.setVisible(false);
            unconmenu.setVisible(true);
            /*button1.setVisibility(View.VISIBLE);
            button2.setVisibility(GONE);
            button3.setVisibility(GONE);*/
        }
        else
        {
            unconmenu.setVisible(false);
            conmenu.setVisible(true);
            /*button1.setVisibility(GONE);
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);*/
        }
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

        stophandler = true;

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
        } else if (id == R.id.nav_9) { // connection
            LoginActivity.classtogo = 8; // ok whatever 6 7 8
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

    // Test connexion au serveur
    class TryConnect extends AsyncTask<Void, Void, Call<ResponseBody>>{

        @Override
        protected Call<ResponseBody> doInBackground(Void...params) {
            service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
            Call<ResponseBody> r = null;
            r = service.getInit();
            return r;
        }

        @Override
        protected void onPostExecute(Call<ResponseBody> repos) {
            super.onPostExecute(repos);
            if(repos == null){
                connexion.setText(R.string.erreur_reception);
                //repoList.clear();
                //repos.clear();
                return;
            }
            else {
                // OK Connexion réussi
                lay_loading.setVisibility(View.INVISIBLE);
                connexion.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                //search.setVisibility(View.VISIBLE);
                checkarticles.setEnabled(true);
                ACCESS = true;
            };
        }
    }

    // listage des objets dans /article
    class ListReposTask extends AsyncTask<Void, Void, List<Article>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            lay_loading.setVisibility(View.VISIBLE);
            loader.setVisibility(View.VISIBLE);
            connexion.setVisibility(View.VISIBLE);
            connexion.setText("Chargement...");
            lay_loading.bringToFront();
        }

        @Override
        protected List<Article> doInBackground(Void...params) {
            service = new Retrofit.Builder()
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
            if(repos == null){
                connexion.setText(R.string.erreur_reception);
                //repoList.clear();
                //repos.clear();
                return;
            }
            else {
                // OK Connexion réussi
                lay_loading.setVisibility(View.INVISIBLE);
                connexion.setVisibility(View.INVISIBLE);
                loader.setVisibility(View.INVISIBLE);
                //search.setVisibility(View.VISIBLE);
                checkarticles.setEnabled(true);
                ACCESS = true;
                afficherArticles(repos);
            };
        }
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    void afficherArticles(List<Article> repos){
        pb.setVisibility(View.GONE);
        moreatic.setVisibility(View.VISIBLE);

        dataModels = new ArrayList<>();

        ApiService service2 = new Retrofit.Builder()
                .baseUrl("http://"+ MainActivity.URLServerImage)
                .build()
                .create(ApiService.class);

        int size = repos.size();
        int limit = size;

        if(showall == false){
            limit = 12;
        }else{
            liste.removeFooterView(footerView);
        }


        if(sortMode==1)Collections.shuffle(repoList);
        if(sortMode==2)Collections.reverse(repoList);
        if(sortMode==3)Collections.sort(repoList,new Sortbyroll());
        if(sortMode==4)Collections.sort(repoList,new Sortbyrollprice());
        if(descmode)Collections.reverse(repoList);

        int results=0;
        outerloop:
        for (int z = 0; z < size; z++) {
            if( repos.get(z).getName().toLowerCase().contains(searchword.getText().toString().toLowerCase()) ) {
                results++;
                if(results > limit && showall == false){
                    Log.i("err","FIN DE LIST showall "+showall+" res "+results+" limit "+limit);
                    liste.addFooterView(footerView);
                    TextView numrestant = footerView.findViewById(R.id.otheraticlesnum);
                    numrestant.setText(Integer.toString(size-12));
                    break outerloop;
                }
                double Price = repos.get(z).getPrice();
                Random r = new Random();
                double randomValue = 10 + (30 - 10) * r.nextDouble();
                if (Price == 0) {
                    Price = randomValue;
                }

                ResponseBody body = null;
                Bitmap imagedata = null;
                try {
                    body = service2.retrieveImageData("article",Integer.valueOf(repos.get(z).getId().toString())).execute().body();
                    if(body != null) {
                        byte[] bytes = new byte[0];
                        try {
                            bytes = body.bytes();
                            imagedata = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        } catch (IOException e) {
                            Log.i("err", "uka uka gros problemes");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    Log.i("err","PAS D'IMAGE OBJ NUM "+Integer.toString(z));
                    e.printStackTrace();
                }

                dataModels.add(new objetEnVente(repos.get(z).getName(), repos.get(z).getDescription(), String.format("%.2f",Price), imagedata, repos.get(z).getLike(), repos.get(z).getUnlike()));
            }
        }

        showall = false;

        if(repos.size()==0){
            Toast.makeText(getApplicationContext(),
                    R.string.no_res,
                    Toast.LENGTH_LONG).show();
        }

        lay_loading.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.INVISIBLE);

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

class Sortbyroll implements Comparator<Article>
{
    @Override
    public int compare(Article article, Article t1) {
        return article.getName().charAt(0) - t1.getName().charAt(0);
    }
}
class Sortbyrollprice implements Comparator<Article>
{
    @Override
    public int compare(Article article, Article t1) {
        return ((int)article.getPrice()*100) - ((int)t1.getPrice()*100);
    }
}