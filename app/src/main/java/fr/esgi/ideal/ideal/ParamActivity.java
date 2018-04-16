package fr.esgi.ideal.ideal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ParamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        final TextView SETACTUEL = (TextView) findViewById(R.id.actualset);
        final EditText IP = (EditText) findViewById(R.id.ipparam);
        final EditText PORT = (EditText) findViewById(R.id.portparam);
        final Button mClickButton1 = (Button) findViewById(R.id.retourparam);

        SETACTUEL.setText("Adresse IP actuelle du serveur : "+MainActivity.URLServer);

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
                MainActivity.URLServer = IP.getText().toString()+":"+PORT.getText().toString();
                finish();
            }
        });
    }
}
