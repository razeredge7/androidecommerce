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

public class MainAdminPaymentLogActivity extends AppCompatActivity {
    TextView payment_alert;
    Button user_list,logout;
    RecyclerView recyclerView;
    ArrayList<PaymentView> datalist;
    DatabaseReference ref_for_trans,ref_for_admin;
    int adminTransCount;
    int currentTransCount;

    @Override
    protected void onPause() {
        super.onPause();
        payment_alert = findViewById(R.id.admin_payment_alert);
        payment_alert.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin_payment_log);

        AlertDialog.Builder builder = new AlertDialog.Builder(this );
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainAdminPaymentLogActivity.this, LoginActivity.class);
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


        user_list = findViewById(R.id.admin_payment_user_list_button);
        logout = findViewById(R.id.admin_payment_logout_btn);
        recyclerView = findViewById(R.id.admin_payment_recycler);
        payment_alert = findViewById(R.id.admin_payment_alert);
        payment_alert.setVisibility(View.INVISIBLE);


        ref_for_admin = FirebaseDatabase.getInstance().getReference().child("admin");
        ref_for_admin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminTransCount = Integer.parseInt(dataSnapshot.child("trans_wait_count").getValue().toString());
                Log.d("admin", "admin count: " + adminTransCount);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ref_for_trans = FirebaseDatabase.getInstance().getReference().child("users");
        ref_for_trans.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //per user
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    String username = snapshot.child("username").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    //pertransaction
                    for (DataSnapshot snapshot1 : snapshot.child("transaction").getChildren() ) {
                        if(snapshot1.child("status").getValue().equals("Waiting Confirmation")){
                            Log.d("admin", "onDataChange: " + snapshot1.child("transaction_Id").getValue().toString());
                            currentTransCount++;
                            PaymentView paymentView = new PaymentView();
                            paymentView.setUsername(username);
                            paymentView.setEmail(email);
                            paymentView.setTrans_id(snapshot1.child("transaction_Id").getValue().toString());
                            paymentView.setMethod(snapshot1.child("payment").getValue().toString());
                            paymentView.setCost(snapshot1.child("total_cost").getValue().toString());
                            paymentView.setTrans_date(snapshot1.child("date").getValue().toString());
                            datalist.add(paymentView);
                        }
                    }
                }
                if (currentTransCount > adminTransCount){
                    int delta = currentTransCount - adminTransCount;
                    payment_alert.setText( delta + " new Payment(s) has recorded");
                    payment_alert.setVisibility(View.VISIBLE);
                }
                Log.d("admin", "onDataChange: current count" +currentTransCount );

                ref_for_admin = FirebaseDatabase.getInstance().getReference().child("admin");
                ref_for_admin.child("trans_wait_count").setValue(currentTransCount);

                TablePaymentAdapter adapter = new TablePaymentAdapter(datalist,MainAdminPaymentLogActivity.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainAdminPaymentLogActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        user_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainAdminPaymentLogActivity.this,MainAdminActivity.class);
                startActivity(intent);

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });


    }
}
