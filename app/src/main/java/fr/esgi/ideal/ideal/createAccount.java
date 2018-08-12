package fr.esgi.ideal.ideal;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import fr.esgi.ideal.ideal.api.ApiService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class createAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Bouton retour : On ferme
        Button retour = (Button) findViewById(R.id.retourcreateacc);

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        // Creation du compte
        final Button save = (Button) findViewById(R.id.savecreateacc);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        R.string.createdacc,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    class CreateObj extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...params) {
            ApiService service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
            /*service.createUser(TITRE.getText().toString(),0,DESC.getText().toString(),Double.parseDouble(PRIX.getText().toString()));
            finish();
            Toast.makeText(getApplicationContext(),
                    R.string.addedprod,
                    Toast.LENGTH_LONG).show();*/

            return null;
        }
    }
}
