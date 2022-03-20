package com.react.reactapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class CommonFunctions extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference mStorageRef;

    @SuppressLint("NonConstantResourceId")
    public static boolean menu(Context con, MenuItem item, String src) {
        Intent i = null;
//        mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Dashboard.class);
                break;
            case R.id.action_covid:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Cases.class);
                break;
            case R.id.action_health:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Health.class);
                break;
            case R.id.action_qrcode:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, QRCode.class);
                break;
            case R.id.action_location:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Location.class);
                break;
            case R.id.action_notifs:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Notifications.class);
                break;
            case R.id.action_settings:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Settings.class);
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails(NavigationView nv) {
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("info");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        View headerView = nv.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.header_username);
        ImageView pic = (ImageView) headerView.findViewById(R.id.header_face);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = String.valueOf(snapshot.child("lName").getValue()) +", "+ String.valueOf(snapshot.child("fName").getValue()) + " " + String.valueOf(snapshot.child("mName").getValue());
                name.setText(String.valueOf(uName));
                String face = (String) snapshot.child("faceID").getValue();
                StorageReference photoRef = mStorageRef.child(face);
                final long ONE_MB = 1024*1024 * 5;
                photoRef.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                    Bitmap bmpLicense = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    pic.setImageBitmap(bmpLicense);
//                    pic.getLayoutParams().height = pic.getLayoutParams().width; remove
                }).addOnFailureListener(e -> {
                    Log.e("photoRef Error", e.toString());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    public int getBrgyIndex(String brgy) {
        List<String> ArrayBrgy = Arrays.asList(getResources().getStringArray(R.array.barangay));
        return ArrayBrgy.indexOf(brgy);
    }

    public Object generateQR(String action) {
        mAuth = FirebaseAuth.getInstance();
        String qr_Address = mAuth.getCurrentUser().getUid();

        File storageDir = new File( Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "REaCT");

        MultiFormatWriter writer = new MultiFormatWriter();

        if(!storageDir.exists()){

            boolean s = new File(storageDir.getPath()).mkdirs();

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

        try {
            BitMatrix matrix = writer.encode(qr_Address, BarcodeFormat.QR_CODE,350,350);

            if(action.equals("imgView")) {
                int height = matrix.getHeight();
                int width = matrix.getWidth();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }
//                ImageView qr_image = (ImageView) findViewById(R.id.qr_imgQR);
//                qr_image.setImageBitmap(bmp);
                return bmp;
            }
            else {
                BarcodeEncoder encoder = new BarcodeEncoder();

                Bitmap bitmap = encoder.createBitmap(matrix);
                try (FileOutputStream out = new FileOutputStream(storageDir + "/QRCode.jpg")) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                    return storageDir + "/REaCT-" + qr_Address + ".jpg";
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
