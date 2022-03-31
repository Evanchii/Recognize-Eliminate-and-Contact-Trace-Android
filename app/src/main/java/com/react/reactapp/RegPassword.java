package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class RegPassword extends AppCompatActivity {

    HashMap<String, String> info;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference signupDbRef;
    CheckBox ToS;
    private ProgressDialog progUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_password);
        Intent intent = getIntent();
        info = (HashMap<String, String>) intent.getSerializableExtra("info");
        mAuth= FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();
        String ip;

        ToS = findViewById(R.id.regPass_chkToS);
        String text = "I agree to the Terms and Service\nand Privacy Policy";
        SpannableString ss = new SpannableString(text);
        ClickableSpan cS1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://react-app.ga/pages/terms.php")));
            }
        };
        ClickableSpan cS2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://react-app.ga/pages/privacy.php")));
            }
        };
        ss.setSpan(cS1, 15, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(cS2, 37, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ToS.setText(ss);
        ToS.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void returnLogin(View view) {
        startActivity(new Intent(RegPassword.this, Login.class));
        finish();
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

    public void Signup(View view) {
        progUp = ProgressDialog.show(this, "Registering","Please wait as we register you in our database.", true);
        progUp.setCancelable(false);
//        Toast.makeText(RegPassword.this, "Signing up...", Toast.LENGTH_LONG).show();
        TextInputEditText pass = findViewById(R.id.password_txtPass),
                conf = findViewById(R.id.password_txtConf);
        TextInputLayout layPass = (TextInputLayout) pass.getParent().getParent(),
                layConf = (TextInputLayout) pass.getParent().getParent();
        layPass.setErrorEnabled(false);
        layConf.setErrorEnabled(false);
        if(pass.getText().toString().trim().equals(conf.getText().toString().trim())) {
            if(pass.getText().toString().trim().isEmpty()) {
                layPass.setError("Field is required");
                layPass.setErrorEnabled(true);
            }
            if(conf.getText().toString().trim().isEmpty()) {
                layPass.setError("Field is required");
                layPass.setErrorEnabled(true);
            }
            //Register
            if(ToS.isChecked()) {
                Log.d("PASSWORD>", info.keySet().toString());
                mAuth.createUserWithEmailAndPassword(info.get("email"), pass.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                mAuth.getCurrentUser().sendEmailVerification();
                                String userID = mAuth.getCurrentUser().getUid();
                                signupDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("info");
                                signupDbRef.child("Type").setValue("visitor");
                                for (String key : info.keySet()) {
                                    if (key.equals("email") || key.equals("faceID") || key.equals("ID")) {
                                        continue;
                                    } else {
                                        signupDbRef.child(key).setValue(info.get(key));
                                    }
                                }
                                signupDbRef.child("status").setValue(false);
                                DatabaseReference logRef = FirebaseDatabase.getInstance().getReference().child("Logs/");
                                logRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Long tsLong = System.currentTimeMillis()/1000;
                                        while(snapshot.hasChild(String.valueOf(tsLong))) {
                                            tsLong++;
                                        }
                                        String ts = tsLong.toString();

                                        HashMap<String, HashMap<String, String>> log = new HashMap<>();

                                        logRef.child(ts).child("ip").setValue("Android System - " + getIPAddress(false));
                                        logRef.child(ts).child("description").setValue(info.get("lName") + ", " +
                                                info.get("fName") + " " + info.get("mName") + "(" +
                                                mAuth.getCurrentUser().getUid() + ") has created their own account");
                                        logRef.child(ts).child("category").setValue("Account");

                                        Uri ID = Uri.parse(info.get("ID")),
                                                face = Uri.parse(info.get("faceID"));

                                        StorageReference filepathFace = mStorage.child("Face").child(mAuth.getCurrentUser().getUid().toString() +"."+ face.getLastPathSegment().split("\\.")[1]);
                                        filepathFace.putFile(face).addOnSuccessListener(taskSnapshot -> {
                                            signupDbRef.child("faceID").setValue("Face/"+mAuth.getCurrentUser().getUid().toString() +"."+ face.getLastPathSegment().split("\\.")[1]);
//                                          progUp.dismiss();
                                            finish();
                                        });

                                        StorageReference filepathID = mStorage.child("ID").child(mAuth.getCurrentUser().getUid().toString() +"."+ ID.getLastPathSegment().split("\\.")[1]);
                                        filepathID.putFile(ID).addOnSuccessListener(taskSnapshot -> {
                                            signupDbRef.child("ID").setValue("ID/"+mAuth.getCurrentUser().getUid().toString() +"."+ ID.getLastPathSegment().split("\\.")[1]);
//                                          progUp.dismiss();
                                        });

                                        try {
                                            enrollFace(userID);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progUp.dismiss();
                        if (e.getMessage().contains("badly formatted")) {
                            //show dialog
                            Snackbar.make(findViewById(R.id.regPass_parent), "Please enter a valid Email Address", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
//                    error.setText("Please enter a valid Email Address");
                        } else if (e.getMessage().contains("address is in use by another account")) {
                            //show dialog
                            Snackbar.make(findViewById(R.id.regPass_parent), "Email Address is already in use by another account!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
//                    error.setText("Email Address is in use by another account!");
//                            Toast.makeText(RegPassword.this, "Email Address is in use by another account", Toast.LENGTH_LONG).show();
                        } else if (e.getMessage().contains("6 character")) {
                            layPass.setError("Password should be at least 6 characters long");
                        } else {
//                            Toast.makeText(RegPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                            Snackbar.make(findViewById(R.id.regPass_parent), e.getMessage(), Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
                        }
                        layPass.setErrorEnabled(true);
                    }
                });
            } else {
                progUp.dismiss();
                Toast.makeText(RegPassword.this, "Terms of Service Agreement Required!", Toast.LENGTH_LONG).show();
            }
        }
        else {//if password not match
            layPass.setError("Passwords does not match");
            layConf.setError("Passwords does not match");
            layPass.setErrorEnabled(true);
            layConf.setErrorEnabled(true);
            progUp.dismiss();
        }
    }

    private void enrollFace(String uid) throws UnsupportedEncodingException, JSONException {
        String app_id = "345b9a6b";
        String api_key = "0ee46186eb4310b5e7936385b2f32a32";
        String gallery_id = "users";

        Kairos myKairos = new Kairos();
        myKairos.setAuthentication(RegPassword.this, app_id, api_key);

        KairosListener kaiListener = new KairosListener() {
            @Override
            public void onSuccess(String response) {
                Log.d("KaiSucc", response);
            }

            @Override
            public void onFail(String response) {
                Log.e("KaiFail", response);
            }
        };

        String selector = "FULL";
        try {
            myKairos.enroll(getThumbnail(Uri.parse(info.get("faceID"))),
                    uid,
                    gallery_id,
                    selector,
                    "false",
                    "0.1",
                    kaiListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

        progUp.dismiss();

        AlertDialog.Builder confEmail = new AlertDialog.Builder(RegPassword.this);
        confEmail.setTitle("One last step left")
                .setMessage("Thank you for registering! Let's finalize your registration by Confirming your Email Address by opening the link we sent to your email inbox/spam.")
                .setPositiveButton("OK", (dialog, which) -> {
                    mAuth.signOut();
                    setResult(Activity.RESULT_OK);
                    finish();
                }).show();
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
            return null;
        }

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 500) ? (originalSize / 500) : 1.0;


        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
}