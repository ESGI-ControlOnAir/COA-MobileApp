package fr.esgi.ideal.ideal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class ParamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        final TextView SETACTUEL = (TextView) findViewById(R.id.actualset);
        final EditText IP = (EditText) findViewById(R.id.ipparam);
        final EditText PORT = (EditText) findViewById(R.id.portparam);
        final Button mClickButton1 = (Button) findViewById(R.id.retourparam);

        String Content_URLServer[] = MainActivity.URLServer.split(":");
        IP.setText(Content_URLServer[0]);
        PORT.setText(Content_URLServer[1]);

        SETACTUEL.setText("Adresse IP actuelle du serveur : "+MainActivity.URLServer);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mClickButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button mClickButton2 = (Button) findViewById(R.id.validparam);
        mClickButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Changement de valeur
                MainActivity.URLServer = IP.getText().toString()+":"+PORT.getText().toString();
                // Sauvegarde pour utilisation utlérieur
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("URLServer",MainActivity.URLServer);
                editor.apply();
                // Fermeture
                finish();
            }
        });
    }
}
