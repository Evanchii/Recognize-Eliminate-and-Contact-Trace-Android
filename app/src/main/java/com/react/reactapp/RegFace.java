package com.react.reactapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_face);
//        Intent intent = getIntent();
//        info = (HashMap<String, String>) intent.getSerializableExtra("userInfo");

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

    public static String getFileToByte(String filePath){
        Bitmap bmp = null;
        ByteArrayOutputStream bos = null;
        byte[] bt = null;
        String encodeString = null;
        try{
            bmp = BitmapFactory.decodeFile(filePath);
            bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bt = bos.toByteArray();
            encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return encodeString;
    }

    public void submit(View view) throws FileNotFoundException {
//        Call API
//        if(true/*success*/) {
//            info.put("faceID", String.valueOf(URIFace));
//            openActivity.launch((new Intent(RegFace.this, RegID.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
//        }
    }
}