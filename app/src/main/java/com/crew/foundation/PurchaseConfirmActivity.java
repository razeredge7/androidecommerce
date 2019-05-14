package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PurchaseConfirmActivity extends AppCompatActivity {

    TextView courierTV,totalcostTV,methodTV,totalitemTV;
    RecyclerView recyclerView;
    Button confirmBtn;
    Bundle bundle;
    String trans_id;
    DatabaseReference ref_for_trans,ref_for_clear_cart;
    private PurchaseAdapter adapter;
    private ArrayList<ShoppingCart> final_cart_list;
    String username_session;
    private Handler WaitHandler = new Handler();
    ProgressBar progressBar;


    public String generateTransID(long count,String id_trans){
        String id = "";
        int plus = (int) count;
        int length = (int)(Math.log10(count)+1);
        Log.d("tesTrans", "generateTransID: " + Integer.toString(length));
        if(length == 1){
            id = "TS-"+ id_trans   +"-0000" + Integer.toString(plus);
        }
        else if (length == 2){
            id = "TS-"+ id_trans  +"-000" + Integer.toString(plus);
        }
        else if(length == 3){
            id = "TS-"+ id_trans   +"-00" + Integer.toString(plus);
        }
        else if(length == 4){
            id = "TS-"+ id_trans   +"-0" + Integer.toString(plus);
        }
        else if(length == 5){
            id = "TS-"+ id_trans   +"-" + Integer.toString(plus);
        }
        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_confirm);

        Log.d("check1", "onCreate: ");
        setTitle("Purchase Confirmation");
        final_cart_list = new ArrayList<>();

        bundle = getIntent().getExtras();
        username_session = bundle.getString("username");
        Log.d("confirm", "username: " + bundle.getString("username"));
        Log.d("confirm", "courier: " + bundle.getString("courier_type"));
        Log.d("confirm", "method: " + bundle.getString("payment_method"));
        Log.d("confirm", "cost: " + bundle.getInt("total_price"));
        Log.d("confirm", "item: " + bundle.getInt("total_item"));
        int total_cost = bundle.getInt("total_price");
        int courier_cost = bundle.getInt("courier_price");
        final int total = total_cost + courier_cost;

        progressBar = findViewById(R.id.purchase_confirm_loading);
        courierTV = findViewById(R.id.transaction_detail_status);
        totalcostTV = findViewById(R.id.transaction_detail_cost);
        totalitemTV = findViewById(R.id.purchase_confirm_item);
        methodTV = findViewById(R.id.purchase_confirm_pay_method);
        recyclerView = findViewById(R.id.purchase_confirm_recycler);
        confirmBtn = findViewById(R.id.purchase_confirm_button);

        final_cart_list = bundle.getParcelableArrayList("shopping_list");
        courierTV.setText(bundle.getString("courier_type"));
        methodTV.setText(bundle.getString("payment_method"));
        totalitemTV.setText(Integer.toString(bundle.getInt("total_item")) + " item(s)" );
        totalcostTV.setText("$" +Integer.toString(total) + ".00" );

        adapter = new PurchaseAdapter(final_cart_list,username_session);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PurchaseConfirmActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                ref_for_trans = FirebaseDatabase.getInstance().getReference().child("users").child(username_session);
                //buat generate id
                ref_for_trans.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        trans_id = dataSnapshot.child("id_for_trans").getValue().toString();
                        long count = dataSnapshot.child("transaction").getChildrenCount();
                        trans_id = generateTransID(count,trans_id);
                        Log.d("check", "onDataChange: jalan");

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                WaitHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("check", "run: " + trans_id);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                            final String currentDate = sdf.format(new Date());
                            Transaction transaction = new Transaction(trans_id,total,bundle.getString("courier_type"), bundle.getString("payment_method"),
                                    bundle.getInt("total_item"),"Waiting Payment",final_cart_list,currentDate);
                            ref_for_trans = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("transaction");
                            ref_for_trans.child(trans_id).setValue(transaction);
                            ref_for_clear_cart = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("cart");
                            ref_for_clear_cart.setValue(null);
                            ref_for_clear_cart.child("placeholder").child("title").setValue("");
                            Intent intent = new Intent(PurchaseConfirmActivity.this,PaymentDetailActivity.class);

                            intent.putExtra("username",username_session);
                            intent.putExtra("payment_method",bundle.getString("payment_method"));
                            intent.putExtra("total_cost",total);
                            intent.putExtra("trans_id",trans_id);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            finish();

                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                        }
                    }
                }, 1500);
            }
        });





    }
}
