package com.crew.foundation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class AccountFragment extends Fragment {

    private Fragment fragment = this;
    Button btnEdit;
    DatabaseReference ref_account_info,ref_pic;
    TextView fullname, username,dob,pob,email,address;
    ImageView profilepic;
    StorageReference getpic;
    String pic_path_in_database;
    ProgressBar progressBar;
    String username_session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ((MainActivity) getActivity())
                .setActionBarTitle("Account Information");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        Log.d("tesman", "onResume: ");
        ref_pic = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("profilepic");
        ref_pic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("tesman", "onDataChange: ");
                pic_path_in_database = dataSnapshot.getValue().toString();
                if (!pic_path_in_database.equals("")) {
                    final long ONE_MEGABYTE = 1024 * 1024;
                    getpic = FirebaseStorage.getInstance().getReferenceFromUrl("gs://uas-ecommerce-c3a79.appspot.com")
                            .child("user_images").child(pic_path_in_database);
                    String tes = getpic.toString();
                    Log.d("pic", "masuk: " + tes);
                    getpic.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.d("pic", "masuk: 2");
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            profilepic.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.account_loading);
        progressBar.setVisibility(View.VISIBLE);
        fullname = view.findViewById(R.id.info_fullname);
        username = view.findViewById(R.id.info_username);
        dob = view.findViewById(R.id.info_dob);
        pob = view.findViewById(R.id.info_pob);
        email = view.findViewById(R.id.info_email);
        address = view.findViewById(R.id.info_address);
        btnEdit = view.findViewById(R.id.info_edit_button);
        profilepic = view.findViewById(R.id.info_image);
        //ambil username
         username_session = getArguments().getString("username");
        //connect ke database dan ambil data sesuai username
        ref_account_info = FirebaseDatabase.getInstance().getReference().child("users").child(username_session);
        ref_account_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fullname_string = dataSnapshot.child("first_name").getValue() + " " +
                        dataSnapshot.child("last_name").getValue().toString();
                fullname.setText(fullname_string);
                username.setText(username_session);
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
                else {
                    progressBar.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditInfoActivity.class);
                intent.putExtra("username",username_session);
                startActivity(intent);

            }
        });

    }
}