package fr.esgi.ideal.ideal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Adpage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adpage);

        // Bouton X : On ferme la pub
        final Button closead = (Button) findViewById(R.id.closead);
        closead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.addisplayed = true;
            }
        });

        // Déjà vu : On passe la pub
        if(MainActivity.addisplayed)
        {
            finish();
        }
    }

}
