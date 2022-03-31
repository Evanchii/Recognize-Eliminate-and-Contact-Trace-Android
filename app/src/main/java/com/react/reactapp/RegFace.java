package com.react.reactapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

public class RegFace extends AppCompatActivity {

    HashMap<String, String> info;
    private Uri URIFace;
    ActivityResultLauncher<Intent> openActivity;
    File sdImageMainDirectory;
    File root;
    String fname;
    private ProgressDialog progUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_face);
        Intent intent = getIntent();
        info = (HashMap<String, String>) intent.getSerializableExtra("userInfo");

        openActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });

        ActivityResultLauncher<Uri> cameraIntent = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        ((ImageView) findViewById(R.id.regFace_imgFace)).setImageURI(URIFace);
                        ((Button) findViewById(R.id.regID_btnNext)).setEnabled(true);
                    }
                });

        Button upload = (Button) findViewById(R.id.regID_btnUpload);
        upload.setOnClickListener(v -> {
            root = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM), "REaCT");

            if (!root.exists()) {

                boolean s = new File(root.getPath()).mkdirs();

                if (!s) {
                    Log.v("not", "not created");
                } else {
                    Log.v("cr", "directory created");
                }
            } else {
                Log.v("directory", "directory exists");
            }

            fname = "img_" + System.currentTimeMillis() + ".jpg";
            sdImageMainDirectory = new File(root, fname);
            URIFace = FileProvider.getUriForFile(
                    RegFace.this,
                    this.getApplicationContext()
                            .getPackageName() + ".provider", sdImageMainDirectory);
            cameraIntent.launch(URIFace);
        });
    }

    public void submit(View view) throws FileNotFoundException, UnsupportedEncodingException, JSONException {
        progUp = ProgressDialog.show(this, "Processing","Please wait as we cross match your face in our database.", true);
        progUp.setCancelable(false);

        BitmapDrawable drawable = (BitmapDrawable) ((ImageView) findViewById(R.id.regFace_imgFace)).getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        String app_id = "345b9a6b";
        String api_key = "0ee46186eb4310b5e7936385b2f32a32";
        String gallery_id = "users";

        Kairos myKairos = new Kairos();
        myKairos.setAuthentication(RegFace.this, app_id, api_key);

        KairosListener kaiListener = new KairosListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("KaiSucc", response);
                progUp.dismiss();
                try {
                    BitmapDrawable drawable = (BitmapDrawable) ((ImageView) findViewById(R.id.regFace_imgFace)).getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    JSONObject json = new JSONObject(response);
                    if(json.has("Errors")) {
                        int code = json.getJSONArray("Errors").getJSONObject(0).getInt("ErrCode");
                        switch (code) {
                            case 5002:
                                Toast.makeText(RegFace.this, "Error 5002: No face detected", Toast.LENGTH_LONG).show();
                                break;
                            case 5010:
                                Toast.makeText(RegFace.this, "Error 5010: Too many faces detected", Toast.LENGTH_LONG).show();
                                break;
                            case 5012:
                            case 5004:
                                info.put("faceID", String.valueOf(URIFace));
                                openActivity.launch((new Intent(RegFace.this, RegID.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                break;
                        }
                    } else if (json.has("images")) {
                        if(json.getJSONArray("images").getJSONObject(0).getJSONObject("transaction").getDouble("confidence") >= 0.75) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegFace.this);
                            builder.setCancelable(true);
                            builder.setTitle("Similar Face Detected");
                            builder.setMessage("The system has detected that someone enrolled in the system may have similar facial features as you, do you still wish to proceed with the registration? Please be aware that creating fake or spam accounts are against the system's Terms of Service.");
                            builder.setPositiveButton("Confirm",
                                    (dialog, which) -> {
                                        info.put("faceID", String.valueOf(URIFace));
                                        openActivity.launch((new Intent(RegFace.this, RegID.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                    });
                            builder.setNegativeButton(android.R.string.cancel, null);

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            info.put("faceID", String.valueOf(URIFace));
                            info.put("faceBitmap", BitMapToString(bitmap));
                            openActivity.launch((new Intent(RegFace.this, RegID.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String response) {
                Log.d("KaiFail", response);
                progUp.dismiss();
            }
        };

        String selector = "FULL";
        myKairos.recognize(bitmap,
                gallery_id,
                selector,
                null,
                "0.1",
                null,
                kaiListener);
//        info.put("faceID", String.valueOf(URIFace));
//        openActivity.launch((new Intent(RegFace.this, RegID.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}