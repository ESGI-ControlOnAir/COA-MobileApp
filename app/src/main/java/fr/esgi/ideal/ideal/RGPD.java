package fr.esgi.ideal.ideal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RGPD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgpd);

        // RGPD est accepté : On ferme
        final Button accepted = (Button) findViewById(R.id.accepttherules);
        accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.acceptedRGDP = true;
                finish();
            }
        });
    }
}
