package fr.esgi.ideal.ideal;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class createObject extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    TextView TITRE;
    TextView DESC;
    TextView PRIX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_object);

        TITRE = findViewById(R.id.titleobjcreate);
        DESC = findViewById(R.id.desccreateobj);
        PRIX = findViewById(R.id.objcreateprice);

        // Creation du compte
        final Button retour = (Button) findViewById(R.id.retourcreateobj);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Creation objet
        final Button img = (Button) findViewById(R.id.buttonLoadPicture);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        // Creation objet
        final Button save = (Button) findViewById(R.id.savecreateobj);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        R.string.addedprod,
                        Toast.LENGTH_LONG).show();
                new CreateObj().execute();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    class CreateObj extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void...params) {
            Log.i("error","1");
            ApiService service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
            Article article = new Article(TITRE.getText().toString(),0,DESC.getText().toString(),Double.parseDouble(PRIX.getText().toString()));
                service.createArticle(MainActivity.AccessToken, article);
            finish();

            return null;
        }
    }
}
