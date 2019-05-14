package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainAdminActivity extends AppCompatActivity {

    Button paymentBtn,LogoutBtn;
    TextView alertNewUser;
    RecyclerView recyclerView;
    DatabaseReference ref_for_user,ref_for_admin;
    ArrayList<UserView> datalist;
    int currentUserCount;
    int adminUserCount;

    public static class regisDateComparator implements Comparator<UserView>
    {
        public int compare(UserView left, UserView right) {
            return left.regisDate.compareTo(right.regisDate);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        alertNewUser = findViewById(R.id.admin_user_alert);
        alertNewUser.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        AlertDialog.Builder builder = new AlertDialog.Builder(this );
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainAdminActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //do nothing
                    }
                });
        final AlertDialog alert = builder.create();

        datalist = new ArrayList<>();

        paymentBtn = findViewById(R.id.admin_payment_button);
        LogoutBtn = findViewById(R.id.admin_log_out_btn);
        recyclerView = findViewById(R.id.admin_recycler);
        alertNewUser = findViewById(R.id.admin_user_alert);
        alertNewUser.setVisibility(View.INVISIBLE);

        ref_for_admin = FirebaseDatabase.getInstance().getReference().child("admin");
        ref_for_admin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminUserCount = Integer.parseInt(dataSnapshot.child("usercount").getValue().toString());
                Log.d("admin", "admin count: " + adminUserCount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ref_for_user = FirebaseDatabase.getInstance().getReference().child("users");
        ref_for_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserCount = (int) dataSnapshot.getChildrenCount();
                Log.d("admin", "current count: " + currentUserCount);
                if (currentUserCount > adminUserCount){
                    int delta = currentUserCount - adminUserCount;
                    alertNewUser.setText( delta + " new user(s) has registered");
                    alertNewUser.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    UserView userView = new UserView();
                    userView.setUsername(snapshot.child("username").getValue().toString());
                    userView.setFirst_name(snapshot.child("first_name").getValue().toString());
                    userView.setLast_name(snapshot.child("last_name").getValue().toString());
                    userView.setEmail(snapshot.child("email").getValue().toString());
                    userView.setRegisDate(snapshot.child("regisDate").getValue().toString());
                    userView.setAddress(snapshot.child("address").getValue().toString());
                    Log.d("admin", "username " + snapshot.child("username").getValue().toString());
                    Log.d("admin", "firstname " + snapshot.child("first_name").getValue().toString());
                    Log.d("admin", "lastname " + snapshot.child("last_name").getValue().toString());
                    Log.d("admin", "email " + snapshot.child("email").getValue().toString());
                    Log.d("admin", "regisdate " + snapshot.child("regisDate").getValue().toString());
                    Log.d("admin", "address " + snapshot.child("address").getValue().toString());
                    datalist.add(userView);
                }
                Collections.sort(datalist,new regisDateComparator());
                TableUserAdapter adapter = new TableUserAdapter(datalist);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainAdminActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);

                ref_for_admin = FirebaseDatabase.getInstance().getReference().child("admin");
                ref_for_admin.child("usercount").setValue(currentUserCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdminActivity.this,MainAdminPaymentLogActivity.class);
                startActivity(intent);

            }
        });
        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });






    }
}
