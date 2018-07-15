package fr.esgi.ideal.ideal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import fr.esgi.ideal.ideal.Crypteur;

public class LoginActivity extends AppCompatActivity  {

    // UI references.
    public static AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ProgressBar LoginProg;
    private Class classe;
    public static int classtogo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        LoginProg = (ProgressBar) findViewById(R.id.LoginProg);
        mPasswordView = (EditText) findViewById(R.id.password);
        final Button createacc = (Button) findViewById(R.id.register_button);

        // Spinner : liste des methodes de cryptage pour le login
        Spinner spinner = findViewById(R.id.cryptmeth);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.cryptlist,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final RequestQueue queue = Volley.newRequestQueue(this);

        final Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //OAUTH2
                Log.i("debug","CLICK");
                LoginProg.setVisibility(View.VISIBLE);
                mEmailSignInButton.setVisibility(View.GONE);
                createacc.setVisibility(View.GONE);
                String url = "http://"+MainActivity.URLServer+"/oauth2/token";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // traitement du token dans la réponse
                                Log.d("Login", "<< IDeal server");
                                String token = null;
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    token = jsonObject.getString("access_token");
                                } catch (JSONException e) {
                                    Log.d("Login", "Erreur parse JSON");
                                }
                                if(!token.isEmpty()){
                                    MainActivity.AccessToken = token;
                                    Toast.makeText(getApplicationContext(),
                                            "SUCCESS! Token: "+token,
                                            Toast.LENGTH_LONG).show();
                                    Intent myIntent = new Intent(LoginActivity.this, AccountActivity.class);
                                    LoginActivity.this.startActivity(myIntent);
                                    finish();
                                } else { Log.d("Login", "Erreur login incorrect"); }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Login", error.getMessage());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("grant_type","password");
                        params.put("client_id","acme1");
                        params.put("username",mEmailView.getText().toString());
                        params.put("password",mPasswordView.getText().toString());
                        params.put("secret","secret_");
                        params.put("scope","nimportequoi");
                        return params;
                    }
                };
                queue.add(postRequest);
            }
        });

        // Creation de compte
        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, createAccount.class);
                LoginActivity.this.startActivity(myIntent);
            }
        });

        // Bouton retour : On ferme
        final Button retour = (Button) findViewById(R.id.retoursignin);
        retour.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Si on est déjà connecté on forward sur l'activité
        if(MainActivity.AccessToken != null) {
            switch (classtogo){
                case 5 : classe = createObject.class;
                    break;
                case 6 : classe = AccountActivity.class;
                    break;
                case 7 : classe = Favpage.class;
                    break;
                default: classe = AccountActivity.class;
                    break;
            }
            Intent myIntent = new Intent(LoginActivity.this, classe);
            LoginActivity.this.startActivity(myIntent);
            finish();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}

