package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    TextView guestLogin;
    TextInputEditText usernameET;
    TextInputEditText passwordET;
    DatabaseReference ref_login,ref_for_admin;
    View view;
    ProgressBar progressBar;

    private Handler WaitHandler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginbutton);
        usernameET = findViewById(R.id.login_username);
        passwordET = findViewById(R.id.login_password);
        signupBtn = findViewById(R.id.signupbutton);
        view = findViewById(android.R.id.content);
        progressBar = findViewById(R.id.login_loading);
        guestLogin = findViewById(R.id.login_as_guest_btn);
        guestLogin.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
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
        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                //kirim username untuk jd seperti session key?
                intent.putExtra("username","guest");
                startActivity(intent);
            }
        });

        //jika login button ditekan
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //buat debug
                if(usernameET.getText().toString().equals("admin")){
                    progressBar.setVisibility(View.VISIBLE);
                    ref_for_admin = FirebaseDatabase.getInstance().getReference().child("admin").child("password");
                    ref_for_admin.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String pass = dataSnapshot.getValue().toString();
                            Log.d("admin", "onDataChange: " + pass);
                            if(pass.equals(passwordET.getText().toString())){
                                Intent intent = new Intent(LoginActivity.this, MainAdminActivity.class);
                                intent.putExtra("username","admin");
                                startActivity(intent);
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                            else {
                                errorSnackbar.show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    //arahin firebase ke table users
                    ref_login = FirebaseDatabase.getInstance().getReference().child("users");
                    ref_login.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //ambil isi dari edtText username dan password
                            final String username = usernameET.getText().toString();
                            String password = passwordET.getText().toString();
                            //looping tiap baris users
                            for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                                String username_check = snapshot.child("username").getValue(String.class);
                                String password_check = snapshot.child("password").getValue(String.class);
                                if (username.equals(username_check) && password.equals(password_check) ){
                                    WaitHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            //kirim username untuk jd seperti session key?
                                            intent.putExtra("username",username);
                                            startActivity(intent);
                                        }
                                    },2000);


                                    //butuh return agar snackbar tidak muncul jika berhasil login
                                    return;
                                }
                            }
                            //tampilkan error message jika salah username/password
                            progressBar.setVisibility(View.GONE);
                            errorSnackbar.show();



                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }


            }
        });

    }
}
