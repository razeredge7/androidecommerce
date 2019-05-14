package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class signupactivity extends AppCompatActivity {

    EditText first_name, last_name,pob,email, username, password, address;
    DatePickerDialog datePickerDialog;
    Button regis_button,dob;
    DatabaseReference ref_sign_up;
    ProgressBar progressBar;
    //function untuk check email valid
    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private Handler WaitHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        //mengambil id masing2 sesuai kebutuhan
        regis_button = findViewById(R.id.register_button);
        first_name = findViewById(R.id.register_first_name);
        last_name = findViewById(R.id.register_last_name);
        pob = findViewById(R.id.register_place_of_birth);
        dob = findViewById(R.id.register_date_of_birth);
        email = findViewById(R.id.register_email);
        username = findViewById(R.id.register_username);
        password = findViewById(R.id.register_pass);
        address = findViewById(R.id.register_address);
        progressBar = findViewById(R.id.register_loading);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sign up success!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(signupactivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();

        // initiate the date picker and a button
        int mYear = 2000; //c.get(Calendar.YEAR); // current year
        int mMonth = 0;//c.get(Calendar.MONTH); // current month
        int mDay =  1;//c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(signupactivity.this,R.style.CustomDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //butuh ini karena month mulai dari 0, biar pas di parse gk salah bulan
                        monthOfYear = monthOfYear + 1;
                        String Stringdate = dayOfMonth + "-" + monthOfYear + "-" + year;
                        //format dari string atas
                        SimpleDateFormat spf = new SimpleDateFormat("d-M-yyyy");
                        Date newDate = null;
                        try {
                            newDate = spf.parse(Stringdate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //format yg baru
                        spf = new SimpleDateFormat("dd-MMM-yyyy");
                        Stringdate = spf.format(newDate);
                        dob.setText(Stringdate);

                    }
                }, mYear, mMonth, mDay);
        // perform click event on edittext datepicker
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
                dob.setError(null);
            }
        });

        //saat register button ditekan
        regis_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                //checking username exist
                final boolean[] checkUsername = new boolean[1];
                final boolean[] checkEmailExist = new boolean[1];
                ref_sign_up = FirebaseDatabase.getInstance().getReference().child("users");
                ref_sign_up.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                            String username_check = snapshot.getKey();
                            String email_exist_check = snapshot.child("email").getValue().toString();
                            Log.d("tes", "onDataChange:" + username_check );
                            Log.d("email", "onDataChange: " + email_exist_check);
                            //true jika ada yg sama
                            if(username.getText().toString().equals(username_check)){
                                Log.d("tes", "masuk true"  + username_check);
                                checkUsername[0] = true;
                                break;
                            }
                            else{
                                Log.d("tes", "masuk false"  + username_check);
                                checkUsername[0] = false;
                            }
                            if(email.getText().toString().equals(email_exist_check)){
                                Log.d("email", "email masuk true"  + email_exist_check);
                                checkEmailExist[0] = true;
                            }
                            else{
                                Log.d("email", "email masuk false"  + email_exist_check);
                                checkEmailExist[0] = false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                //kayaknya kasih loading disini

                //butuh ini biar code dibawah gk langsung jalan
                WaitHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //check firstname empty
                            if(first_name.getText().toString().trim().isEmpty()){
                                first_name.setError("This field is required");
                            }
                            //check lastname empty
                            if(last_name.getText().toString().trim().isEmpty()){
                                last_name.setError("This field is required");
                            }
                            final boolean[] checkEmail = {isEmailValid(email.getText().toString())};
                            //check email empty
                            if(email.getText().toString().trim().isEmpty()){
                                email.setError("This field is required");
                            }
                            //check email valid
                            else if(checkEmail[0] == false){
                                Log.d("email", "onClick: masuk valid");
                                email.setError("Please enter a valid email address");
                            }
                            //check email exist
                            else if(checkEmailExist[0] == true){
                                Log.d("email", "onClick: masuk exist");
                                email.setError("This email already exist");
                            }
                            //check address empty
                            if(address.getText().toString().trim().isEmpty()){
                                address.setError("This field is required");
                            }
                            //check pob emty
                            if(pob.getText().toString().trim().isEmpty()){
                                pob.setError("This field is required");
                            }
                            //check dob empty
                            if(dob.getText().toString().trim().isEmpty()) {
                                Log.d("dob", "onClick: " + dob.getText().toString().trim());
                                dob.setError("This field is required");
                            }
                            Log.d("tes", "masuk sini" + checkUsername[0]);
                            //check username empty
                            if(username.getText().toString().trim().isEmpty()){
                                username.setError("This field is required");
                            }
                            else if (username.getText().toString().trim().equals("guest") || username.getText().toString().trim().equals("admin")){
                                username.setError("You cannot use this username");
                            }
                            //check username exist
                            else if(checkUsername[0] == true ){
                                Log.d("tes", "onClick: masuk checkkusername");
                                username.setError("This Username already exist");
                            }

                            //untuk constraint password
                            String s = password.getText().toString();
                            //check apakah ada number/uppercase/lowercase di string
                            boolean uppercase_flag = false;
                            boolean lowercase_flag = false;
                            boolean number_flag = false;
                            Character ch;
                            for (int i = 0; i < s.length(); i++){
                                ch = s.charAt(i);
                                if(Character.isDigit(ch)){
                                    number_flag = true;
                                }
                                else if(Character.isUpperCase(ch)){
                                    uppercase_flag = true;
                                }
                                else if(Character.isLowerCase(ch)){
                                    lowercase_flag = true;
                                }
                            }
                            //check password empty
                            if(password.getText().toString().trim().isEmpty()){
                                password.setError("This field is required");
                            }
                            //constrain panjang password
                            else if(s.length() < 8 ){
                                Log.d("tes", "ceklenght");
                                password.setError("Password must have at least 8 characters");
                            }
                            //constraint angka
                            else if(number_flag == false){
                                Log.d("tes", "ceknumber");
                                password.setError("Password must contain at least one number digit");
                            }
                            //constrain upppercase
                            else if(uppercase_flag == false){
                                Log.d("tes", "cekuppercase");
                                password.setError("Password must contain at least uppercase letter");
                            }
                            else if(lowercase_flag == false){
                                Log.d("tes", "ceklowercase");
                                password.setError("Password must contain at least lowercase letter");
                            }
                            if(first_name.getError() == null && last_name.getError() == null &&
                                    email.getError() == null && pob.getError() == null && address.getError() == null &&
                                    dob.getError() == null && username.getError() == null &&
                                    password.getError() == null){
                                Users user = new Users();
                                user.setUsername(username.getText().toString());
                                user.setPassword(password.getText().toString());
                                user.setFirst_name(first_name.getText().toString());
                                user.setLast_name(last_name.getText().toString());
                                user.setDob(dob.getText().toString());
                                user.setPob(pob.getText().toString());
                                user.setEmail(email.getText().toString());
                                user.setAddress(address.getText().toString());
                                //buat regis date
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm");
                                String currentDate = sdf.format(new Date());
                                user.setRegisDate(currentDate);
                                int x = (int)(Math.random()*((999998-100000)+1))+1000000;
                                user.setId_for_trans(Integer.toString(x));
                                //connect ke firebase
                                ref_sign_up = FirebaseDatabase.getInstance().getReference().child("users");
                                ref_sign_up.child(username.getText().toString()).setValue(user);
                                ref_sign_up.child(username.getText().toString()).child("profilepic").setValue("");
                                ref_sign_up.child(username.getText().toString()).child("cart").child("placeholder").child("title").setValue("");
                                ref_sign_up.child(username.getText().toString()).child("wishlist").child("placeholder").setValue("");
                                ref_sign_up.child(username.getText().toString()).child("transaction").child("placeholder").child("transaction_Id").setValue("");
                                ref_sign_up.child(username.getText().toString()).child("transaction").child("placeholder").child("status").setValue("");
                                alert.show();
                            }
                            progressBar.setVisibility(View.GONE);
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                }, 3000);
            }
        });
    }
}
