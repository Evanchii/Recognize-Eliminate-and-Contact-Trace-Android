package com.react.reactapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.HashMap;

public class RegID extends AppCompatActivity {

    HashMap<String, String> info;
    private Uri URIid;
    ActivityResultLauncher<Intent> openActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_id);
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

    public void showList(View view) {
        AlertDialog.Builder list = new AlertDialog.Builder(view.getContext());
        list.setMessage(Html.fromHtml("<ol>" +
                "            <li>e-Card / UMID</li>" +
                "            <li>Employee’s ID / Office Id</li>" +
                "            <li>Driver’s License</li>" +
                "            <li>Professional Regulation Commission (PRC) ID </li>" +
                "            <li>Passport </li>" +
                "            <li>Senior Citizen ID</li>" +
                "            <li>SSS ID</li>" +
                "            <li>COMELEC / Voter’s ID / COMELEC Registration Form</li>" +
                "            <li>Philippine Identification (PhilID)</li>" +
                "            <li>NBI Clearance </li>" +
                "            <li>Integrated Bar of the Philippines (IBP) ID</li>" +
                "            <li>Firearms License </li>" +
                "            <li>AFPSLAI ID </li>" +
                "            <li>PVAO ID</li>" +
                "            <li>AFP Beneficiary ID</li>" +
                "            <li>BIR (TIN)</li>" +
                "            <li>Pag-ibig ID</li>" +
                "            <li>Person’s With Disability (PWD) ID</li>" +
                "            <li>Solo Parent ID</li>" +
                "            <li>Pantawid Pamilya Pilipino Program (4Ps) ID </li>" +
                "            <li>Barangay ID </li>" +
                "            <li>Philippine Postal ID </li>" +
                "            <li>Phil-health ID</li>" +
                "            <li>School ID </li>" +
                "            <li>Other valid government-issued IDs or</li>" +
                "            <li>Documents with picture and address</li>" +
                "            </ol>"))
                .setTitle("List of Valid IDs").setPositiveButton("OK", null).show();
    }

    public void submit(View view) {
        //Call API
        if(true/*success*/) {
            info.put("ID", String.valueOf(URIid));
            openActivity.launch((new Intent(RegID.this, RegPassword.class)).putExtra("info", info).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        }
    }
}