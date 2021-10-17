package com.react.reactapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

public class RegID extends AppCompatActivity {

    HashMap<String, String> info;
    private Uri URIid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_id);
        Intent intent = getIntent();
        info = (HashMap<String, String>) intent.getSerializableExtra("info");

        ActivityResultLauncher<Uri> cameraIntent = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if(result) {
                        ((ImageView) findViewById(R.id.regID_imgID)).setImageURI(URIid);
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
            URIid = FileProvider.getUriForFile(
                    RegID.this,
                    this.getApplicationContext()
                            .getPackageName() + ".provider", sdImageMainDirectory);
            cameraIntent.launch(URIid);
        });
    }

    public void submit(View view) {
        //Call API
        if(true/*success*/) {
            System.out.print("Debug"+info.keySet());
            info.put("ID", String.valueOf(URIid));
            startActivity((new Intent(RegID.this, RegPassword.class)).putExtra("info", info));
            finish();
        }
    }
}