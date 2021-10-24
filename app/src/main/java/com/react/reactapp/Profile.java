package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference mStorageRef;
    boolean edit = false;
    Spinner brgy;
    ImageView pic;
    EditText cNo, bday, add1;
    TextView name, email, addCiCo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");
        setContentView(R.layout.profile);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("info");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        pic = findViewById(R.id.profile_imgDP);
        name = findViewById(R.id.profile_txtName);
        email = findViewById(R.id.profile_txtEmail);

        cNo = findViewById(R.id.profile_txtcNo);
        bday = findViewById(R.id.profile_txtDoB);
        add1 = findViewById(R.id.profile_txtAdd1);
        addCiCo = findViewById(R.id.profile_addCiCo);

        brgy = findViewById(R.id.profile_addBa);
        brgy.setEnabled(false);

        getData();
    }

    public void getData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<String> ArrayBrgy = Arrays.asList(getResources().getStringArray(R.array.barangay));

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

                name.setText(snapshot.child("lName").getValue().toString()+ ", "+snapshot.child("fName").getValue().toString()+" "+snapshot.child("mName").getValue().toString());
                email.setText(mAuth.getCurrentUser().getEmail());
                cNo.setText(snapshot.child("cNo").getValue().toString());
                bday.setText(snapshot.child("DoB").getValue().toString());
                add1.setText(snapshot.child("addNo").getValue().toString());
                addCiCo.setText(snapshot.child("addCi").getValue().toString()+"\n"+snapshot.child("addCo").getValue().toString());
                brgy.setSelection(ArrayBrgy.indexOf(snapshot.child("addBa").getValue()));
                Log.d("Profile> ", String.valueOf(ArrayBrgy.indexOf(snapshot.child("addBa").getValue())));
                Log.d("ProfileBRGY> ", (String) snapshot.child("addBa").getValue());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    public void setData() {
        dbRef.child("cNo").setValue(cNo.getText().toString());
        dbRef.child("DoB").setValue(bday.getText().toString());
        dbRef.child("addNo").setValue(add1.getText().toString());
        dbRef.child("addBa").setValue(brgy.getSelectedItem().toString());

    }

    public void changeMode(View view) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.profile_fabEdit);

        if(edit) {
            //call on destroy to save or discard settings(if press back)
            AlertDialog.Builder confirm = new AlertDialog.Builder(Profile.this)
                    .setTitle("Confirm Changes?")
                    .setMessage("Do you wish to update your information?")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!cNo.getText().toString().isEmpty() && !bday.getText().toString().isEmpty()
                                    && !add1.getText().toString().isEmpty()) {
                                setData();
                                cNo.setEnabled(false);
                                bday.setEnabled(false);
                                add1.setEnabled(false);
                                brgy.setEnabled(false);
                                fab.setImageResource(R.drawable.ic_pencil);
                            }
                            else {
                                Toast.makeText(Profile.this, "Please provide all required data!", Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getData();
                            cNo.setEnabled(false);
                            bday.setEnabled(false);
                            add1.setEnabled(false);
                            brgy.setEnabled(false);
                            fab.setImageResource(R.drawable.ic_pencil);
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            confirm.show();
        } else {
            cNo.setEnabled(true);
            bday.setEnabled(true);
            add1.setEnabled(true);
            brgy.setEnabled(true);
            fab.setImageResource(R.drawable.ic_check);
        }
        edit = !edit;
    }
}