package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    Button signupBtn;
    TextInputEditText usernameET;
    TextInputEditText passwordET;
    DatabaseReference mDatabase;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginbutton);
        usernameET = findViewById(R.id.login_username);
        passwordET = findViewById(R.id.login_password);
        signupBtn = findViewById(R.id.signupbutton);
        view = findViewById(android.R.id.content);
        //snackbar buat error login
        final Snackbar errorSnackbar = Snackbar.make(view,"Username atau Password salah",2000);

        //jika sign up button ditekan
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, signupactivity.class);
                startActivity(intent);
                return;
            }
        });

        //jika login button ditekan
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //arahin firebase ke table users
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //ambil isi dari edtText username dan password
                        String username = usernameET.getText().toString();
                        String password = passwordET.getText().toString();
                        //looping tiap baris users
                        for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                            String username_check = snapshot.child("username").getValue(String.class);
                            String password_check = snapshot.child("password").getValue(String.class);
                            if (username.equals(username_check) && password.equals(password_check) ){
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                //butuh return agar snackbar tidak muncul jika berhasil login
                                return;
                            }
                        }
                        //tampilkan error message jika salah username/password
                        errorSnackbar.show();



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });



            }
        });

    }
}
