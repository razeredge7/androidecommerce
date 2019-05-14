package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Locale;

public class EditInfoActivity extends AppCompatActivity {

    String username_session,profilepic_path,pic_path_in_database;
    EditText first_name, last_name, dob,pob,email,address;
    DatabaseReference ref_edit_info,refforpic;
    StorageReference storageReference,getpic,deletePrev;
    Button saveEdit,editPic;
    ImageView profilepic;
    ProgressBar progressBar;
    Boolean pic_changed;

    private Handler WaitHandler = new Handler();
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;


    private void chooseImage(){
        //mulai pilih image dari local storage hp
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profilepic.setImageBitmap(bitmap);
                pic_changed = true;
                Log.d("picchanged", "pic changed 1: " + pic_changed);


            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        progressBar = findViewById(R.id.edit_loading);
        setTitle("Edit Information");
        pic_changed = false;
        //ambil username;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            username_session = bundle.getString("username");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Edit success!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alert = builder.create();
        progressBar.setVisibility(View.VISIBLE);
        first_name = findViewById(R.id.edit_first_name);
        last_name = findViewById(R.id.edit_last_name);
        dob = findViewById(R.id.edit_dob);
        pob = findViewById(R.id.edit_pob);
        email = findViewById(R.id.edit_email);
        address = findViewById(R.id.edit_address);
        saveEdit = findViewById(R.id.edit_save_button);
        editPic = findViewById(R.id.edit_change_picture_button);
        profilepic = findViewById(R.id.info_image);
        //connect ke database dan ambil data sesuai username
        ref_edit_info = FirebaseDatabase.getInstance().getReference().child("users").child(username_session);
        ref_edit_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("tes1", "jalan sekali doang ");
                first_name.setText(dataSnapshot.child("first_name").getValue().toString());
                last_name.setText(dataSnapshot.child("last_name").getValue().toString());
                dob.setText(dataSnapshot.child("dob").getValue().toString());
                pob.setText(dataSnapshot.child("pob").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString());
                pic_path_in_database = dataSnapshot.child("profilepic").getValue().toString();
                if(!pic_path_in_database.equals("")){
                    final long ONE_MEGABYTE = 1024 * 1024;
                    getpic = FirebaseStorage.getInstance().getReferenceFromUrl("gs://uas-ecommerce-c3a79.appspot.com")
                            .child("user_images").child(pic_path_in_database);
                    String tes = getpic.toString();
                    Log.d("pic", "masuk: " + tes);
                    getpic.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d("pic", "masuk: 2");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            profilepic.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    progressBar.setVisibility(View.GONE);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                // insert update database here
                final String fn = first_name.getText().toString();
                final String ln = last_name.getText().toString();
                final String dateBirth = dob.getText().toString();
                final String placeBirth = pob.getText().toString();
                final String em = email.getText().toString();
                final String adrs = address.getText().toString();

                ref_edit_info = FirebaseDatabase.getInstance().getReference().child("users");
                ref_edit_info.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                            String email_exist_check = snapshot.child("email").getValue().toString();
                            Log.d("email", "onDataChange: " + email_exist_check);
                            //true jika ada yg sama
                            if(email.getText().toString().equals(email_exist_check)){
                                Log.d("email", "email masuk true"  + email_exist_check);
                                email.setError("This email already exist");
                            }
                            else{
                                Log.d("email", "email masuk false"  + email_exist_check);
                                email.setError(null);
                            }
                        }
                        //check apakah string email gk diganti
                        if(em.equals(dataSnapshot.child(username_session).child("email").getValue().toString() ) ) {
                            Log.d("email", "masuk last "  + em + " plus " + dataSnapshot.child(username_session).child("email").getValue().toString() );
                            email.setError(null);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                //jika email gk ada yang sama
                WaitHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(email.getError() == null){
                                Log.d("email", "no error");
                                ref_edit_info.child(username_session).child("first_name").setValue(fn);
                                ref_edit_info.child(username_session).child("last_name").setValue(ln);
                                ref_edit_info.child(username_session).child("dob").setValue(dateBirth);
                                ref_edit_info.child(username_session).child("pob").setValue(placeBirth);
                                ref_edit_info.child(username_session).child("address").setValue(adrs);
                                ref_edit_info.child(username_session).child("email").setValue(em);
                                profilepic_path = "PP-" + username_session;
                                pic_path_in_database = profilepic_path;
                                refforpic = FirebaseDatabase.getInstance().getReference().child("users").child(username_session);
                                refforpic.child("profilepic").setValue(pic_path_in_database);
                                if(pic_changed){
                                    Log.d("picchanged", "pic changed 2: " + pic_changed);
                                    //bagian upload ke firebase
                                    final ProgressDialog progressDialog = new ProgressDialog(EditInfoActivity.this);
                                    progressDialog.setTitle("Uploading...");
                                    progressDialog.show();


                                    //connect ke firebase storage
                                    StorageReference ref = storageReference.child("user_images").child(profilepic_path);
                                    ref.putFile(filePath)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(EditInfoActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    alert.show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(EditInfoActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                                            .getTotalByteCount());
                                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                                }
                                            });
                                }
                                else{
                                    alert.show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },3000);
            }

        });
    }
}
