package fr.esgi.ideal.ideal;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import fr.esgi.ideal.ideal.api.ApiService;
import fr.esgi.ideal.ideal.api.Article;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static java.lang.Thread.sleep;

public class createObject extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    TextView TITRE;
    TextView DESC;
    TextView PRIX;
    ImageView articleprev;
    String picturePath;
    int ObjectID;
    SharedPreferences preferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_object);

        TITRE = findViewById(R.id.titleobjcreate);
        DESC = findViewById(R.id.desccreateobj);
        PRIX = findViewById(R.id.objcreateprice);
        articleprev = findViewById(R.id.articlepreview);

        // Creation du compte
        /*final Button retour = (Button) findViewById(R.id.retourcreateobj);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // ajout image
        final Button img = (Button) findViewById(R.id.buttonLoadPicture);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("img","1");
                articleprev.setVisibility(View.VISIBLE);

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
                LinearLayout lay = findViewById(R.id.bottomcreateobj);
                lay.setVisibility(View.GONE);
                ProgressBar pb = findViewById(R.id.progresscreateobj);
                pb.setVisibility(View.VISIBLE);

                new CreateObj().execute();
                //new SendIMG().execute();
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
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {
                        String reponseart = response.body().string();
                        Log.i("err","REPONSE : "+reponseart);
                        if(response.body() != null){
                            //try {
                            Log.i("err","try img");
                            JSONObject json = null;
                            try {
                                json = new JSONObject(reponseart);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                ObjectID = json.getInt("id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                            String url = "http://"+MainActivity.URLServerImage+"/upload";
                            Log.i("err","img URL:"+url);
                            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    String resultResponse = new String(response.data);
                                    Log.i("err","img result : "+resultResponse);

                                    String savedobjects = preferences.getString("MyObjects", "");
                                    String savedobjectsdates = preferences.getString("MyObjectsDates", "");

                                    Time now = new Time();
                                    now.setToNow();

                                    SharedPreferences.Editor editor = preferences.edit();
                                    if(savedobjects.isEmpty()){
                                        editor.putString("MyObjects",Integer.toString(ObjectID));
                                        editor.putString("MyObjects",Integer.toString(now.monthDay)+"/"+Integer.toString(now.month)+"/"+Integer.toString(now.year)+" "+now.format("%k:%M:%S"));
                                    }
                                    else {
                                        editor.putString("MyObjects",savedobjects+";"+Integer.toString(ObjectID));
                                        editor.putString("MyObjects",savedobjectsdates+";"+Integer.toString(now.monthDay)+"/"+Integer.toString(now.month)+"/"+Integer.toString(now.year)+" "+now.format("%k:%M:%S"));
                                    }
                                    editor.apply();

                                    Toast.makeText(getApplicationContext(),
                                            R.string.addedprod,
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                    // parse success output
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("err","shhit doesntwork2");
                                    if (error.networkResponse == null) {
                                        if (error.getClass().equals(TimeoutError.class)) {
                                            // Show timeout error message
                                            Toast.makeText(getBaseContext(),
                                                    "Oops. Timeout error!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    error.printStackTrace();
                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("filename", "article-"+Integer.toString(ObjectID));
                                    Log.i("err","article-"+Integer.toString(ObjectID));
                                    return params;
                                }

                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                protected Map<String, DataPart> getByteData() {
                                    Map<String, DataPart> params = new HashMap<>();
                                    // file name could found file base or direct access from real path
                                    // for now just get bitmap data from ImageView
                                    File file = new File(picturePath);

                                    Filename imagefile = new Filename(picturePath, '/', '.');

                                    int size = (int) file.length();
                                    byte[] bytes = new byte[size];
                                    try {
                                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                                        buf.read(bytes, 0, bytes.length);
                                        buf.close();
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    //try {
                                        params.put("file", new DataPart("article-"+Integer.toString(ObjectID), bytes, "*/*"));
                                    /*} catch (IOException e) {
                                        e.printStackTrace();
                                    }*/


                                    return params;
                                }
                            };
                            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(100000 * 60, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                            queue.add(multipartRequest);
                            //VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

                            /*try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                                //IMAGE
                                /*ApiService service2 = new Retrofit.Builder()
                                        .baseUrl("http://"+ MainActivity.URLServerImage)
                                        //convertie le json automatiquement
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()
                                        .create(ApiService.class);

                                File file = new File(picturePath);

                                RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), "article-"+Integer.toString(ObjectID));

                                Log.d("err", "7");
                                Call<ResponseBody> req = service2.postImage(body,filename);
                                Log.d("err", "8");
                                req.execute();
                                Log.d("err", "9");

                                /*req.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Log.d("err", "Reached this place");
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        t.printStackTrace();
                                        Log.i("err","ERR upload image");
                                    }
                                });*/
                            /*} catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("err", "10");
                            }*/
                        }


                    } catch (IOException e) {
                        Log.i("err","shit3");
                        e.printStackTrace();

                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i("err","shit2");
                    t.printStackTrace();
                }
            });

            return article;
        }
    }

    /*class SendIMG extends AsyncTask<Void, Void, Call<ResponseBody>>{

        @Override
        protected Call<ResponseBody> doInBackground(Void...params) {
            ApiService service2 = new Retrofit.Builder()
                    .baseUrl("http://"+ MainActivity.URLServer)
                    //convertie le json automatiquement
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);

            File file = new File(picturePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), "article-"+Integer.toString(3));

            Call<ResponseBody> r = null;
            r = service2.postImage(body,filename);
            return r;
        }

        @Override
        protected void onPostExecute(Call<ResponseBody> repos) {
            super.onPostExecute(repos);
            if(repos == null){
                Log.i("err","shit");
                return;
            }
            else {
                // OK Connexion réussi
                Log.i("err","ok");
            };
        }
    }*/

}

class Filename {
    private String fullPath;
    private char pathSeparator, extensionSeparator;

    public Filename(String str, char sep, char ext) {
        fullPath = str;
        pathSeparator = sep;
        extensionSeparator = ext;
    }

    public String extension() {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }

    public String filename() { // gets filename without extension
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }

    public String path() {
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(0, sep);
    }
}
