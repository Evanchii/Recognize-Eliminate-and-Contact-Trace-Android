package com.react.reactapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class VaccinationApplication extends AppCompatActivity {

    ActivityResultLauncher<Intent> openActivity;
    Uri URICard;
    ProgressDialog progUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Vaccine Application");
        setContentView(R.layout.vaccination_application);

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
                        ((ImageView) findViewById(R.id.vacc_imgCard)).setImageURI(URICard);
                        ((Button) findViewById(R.id.vacc_btnSubmit)).setEnabled(true);
                    }
                });

        Button upload = (Button) findViewById(R.id.vacc_btnUpload)
                , submit =(Button)  findViewById(R.id.vacc_btnSubmit);

        upload.setOnClickListener(view -> {
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
            URICard = FileProvider.getUriForFile(
                    VaccinationApplication.this,
                    this.getApplicationContext()
                            .getPackageName() + ".provider", sdImageMainDirectory);
            cameraIntent.launch(URICard);
        });

        submit.setOnClickListener(view -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(),
                userRef = dbRef.child("Users/"+mAuth.getCurrentUser().getUid()+"/info"),
                appRef = dbRef.child("Applications/"),
                logRef = dbRef.child("Logs");
            StorageReference mStorage= FirebaseStorage.getInstance().getReference();

            progUp = ProgressDialog.show(this, "Submitting Application","Please wait as we save your application in our database.", true);
            progUp.setCancelable(false);

            Spinner brand = (Spinner) findViewById(R.id.vacc_spnBrand),
                    status = (Spinner) findViewById(R.id.vacc_spnStatus);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Long tsLong = System.currentTimeMillis()/1000;
                    while(snapshot.hasChild(String.valueOf(tsLong))) {
                        tsLong++;
                    }
                    String ts = tsLong.toString();

                    appRef.child(ts+"/name").setValue(snapshot.child("lName").getValue().toString() + ", "+
                                    snapshot.child("fName").getValue().toString() + " "+
                                    snapshot.child("mName").getValue().toString());
                    appRef.child(ts+"/type").setValue("Vaccination Confirmation");
                    appRef.child(ts+"/uid").setValue(mAuth.getCurrentUser().getUid());
                    appRef.child(ts+"/usertype").setValue("Visitor");
                    appRef.child(ts+"/vaccBrand").setValue(brand.getSelectedItem().toString());
                    appRef.child(ts+"/vaccStatus").setValue(status.getSelectedItem().toString());

                    logRef.child(ts+"/ip").setValue("Android System - " + getIPAddress(false));
                    logRef.child(ts+"/description").setValue(snapshot.child("lName").getValue().toString() + ", "+
                            snapshot.child("fName").getValue().toString() + " "+
                            snapshot.child("mName").getValue().toString() + "(" +
                            mAuth.getCurrentUser().getUid() + ") has submitted their Vaccination Confirmation application");
                    logRef.child(ts).child("category").setValue("Application");

                    StorageReference filepathCard = mStorage.child("Vacc").child(mAuth.getCurrentUser().getUid().toString() +"."+ URICard.getLastPathSegment().split("\\.")[1]);
                    filepathCard.putFile(URICard).addOnSuccessListener(taskSnapshot -> {
                        userRef.child("vaccID").setValue("vacc/"+mAuth.getCurrentUser().getUid().toString() +"."+ URICard.getLastPathSegment().split("\\.")[1]);
                        appRef.child(ts+"/vaccID").setValue("vacc/"+mAuth.getCurrentUser().getUid().toString() +"."+ URICard.getLastPathSegment().split("\\.")[1]);

                        progUp.dismiss();
                        AlertDialog.Builder feedback = new AlertDialog.Builder(VaccinationApplication.this);
                        feedback.setTitle("Application Submitted!")
                                .setMessage("Thank you for sending you Application! We will send you a notification after the administrator reviews your application.")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    finish();
                                }).setCancelable(false).show();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}