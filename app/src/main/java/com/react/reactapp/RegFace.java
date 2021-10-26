package com.react.reactapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;

public class RegFace extends AppCompatActivity {

    HashMap<String, String> info;
    private Uri URIFace;
    ActivityResultLauncher<Intent> openActivity;

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
                    if(result) {
                        ((ImageView) findViewById(R.id.regFace_imgFace)).setImageURI(URIFace);
                        ((Button) findViewById(R.id.regID_btnNext)).setEnabled(true);
                    }
                });

        Button upload = (Button) findViewById(R.id.regID_btnUpload);
        upload.setOnClickListener(v -> {
            File root = new File( Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "REaCT");

            if(!root.exists()){

                boolean s = new File(root.getPath()).mkdirs();

                if(!s){
                    Log.v("not", "not created");
                }
                else{
                    Log.v("cr","directory created");
                }
            }
            else{
                Log.v("directory", "directory exists");
            }

            String fname = "img_" + System.currentTimeMillis() + ".jpg";
            File sdImageMainDirectory = new File(root, fname);
            URIFace = FileProvider.getUriForFile(
                    RegFace.this,
                    this.getApplicationContext()
                            .getPackageName() + ".provider", sdImageMainDirectory);
            cameraIntent.launch(URIFace);
        });
    }

    public void submit(View view) {
        //Call API
        if(true/*success*/) {
            info.put("faceID", String.valueOf(URIFace));
            openActivity.launch((new Intent(RegFace.this, RegID.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }
    }
}