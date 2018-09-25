package fr.esgi.ideal.ideal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class createObject extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    TextView TITRE;
    TextView DESC;
    TextView PRIX;
    ImageView articleprev;
    String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_object);

        TITRE = findViewById(R.id.titleobjcreate);
        DESC = findViewById(R.id.desccreateobj);
        PRIX = findViewById(R.id.objcreateprice);
        articleprev = findViewById(R.id.articlepreview);

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
                Log.i("img","1");
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                try {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(createObject.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RESULT_LOAD_IMAGE);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }


                startActivityForResult(i, RESULT_LOAD_IMAGE);
                Log.i("img","2 GO");
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
        Log.i("img","3");
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Log.i("img","4");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();



            articleprev.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Log.i("img","6 "+picturePath);

        }
    }

    class CreateObj extends AsyncTask<Void, Void, Article> {

        @Override
        protected Article doInBackground(Void...params) {
            Log.i("error","1");
            ApiService service = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
            Article article = new Article(TITRE.getText().toString(),DESC.getText().toString(),Double.parseDouble(PRIX.getText().toString()));
            Call<ResponseBody> call = service.createArticle(MainActivity.AccessToken, article);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
            //IMAGE
            ApiService service2 = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServerImage)
                    //convertie le json automatiquement
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);

            File file = new File(picturePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

            retrofit2.Call<okhttp3.ResponseBody> req = service2.postImage(body, name);
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) { }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Log.i("err","ERR upload image");
                }
            });

            return null;
        }
    }
}
