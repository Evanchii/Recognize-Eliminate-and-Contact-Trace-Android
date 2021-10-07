package com.react.reactapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.HashMap;

public class RegID extends AppCompatActivity {

    HashMap<String, String> info;
    private static final int GALLERY_INTENT=2;
    private Uri URIid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_id);
        Intent intent = getIntent();
        info = (HashMap<String, String>) intent.getSerializableExtra("userInfo");

        Button upload = (Button) findViewById(R.id.regID_btnUpload);
        upload.setOnClickListener(v -> {
            startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/"), GALLERY_INTENT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            URIid= data.getData();
            ((ImageView) findViewById(R.id.regFace_imgFace)).setImageURI(URIid);

            ((Button) findViewById(R.id.regID_btnNext)).setEnabled(true);
        }
    }

    public void submit(View view) {
        //Call API
        if(true/*success*/) {
            info.put("ID", String.valueOf(URIid));
            startActivity((new Intent(RegID.this, RegPassword.class)).putExtra("info", info));
            finish();
        }
    }
}