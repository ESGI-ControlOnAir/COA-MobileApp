package fr.esgi.ideal.ideal;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        Toast.makeText(getApplicationContext(),
                R.string.logout1,
                Toast.LENGTH_LONG).show();
        MainActivity.AccessToken = null;
        MainActivity.acceptedRGDP = false;

        finish();
        navigateUpTo(new Intent(this, MainActivity.class));
    }
}
