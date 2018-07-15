package fr.esgi.ideal.ideal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class createAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Log.i("fffff","11111");

        // Bouton retour : On ferme
        Button retour = (Button) findViewById(R.id.retourcreateacc);
        Log.i("fffff","2222 "+retour.getText());

        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("fffff","jenecomprendspas");
                finish();
            }
        });

        // Creation du compte
        final Button save = (Button) findViewById(R.id.savecreateacc);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ...
            }
        });
    }
}
