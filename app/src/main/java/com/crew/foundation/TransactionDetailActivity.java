package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
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


public class TransactionDetailActivity extends AppCompatActivity {

    TextView trans_id,trans_date,trans_status,trans_courier,trans_cost,trans_bank,trans_item,pay_btn;
    RecyclerView recyclerView;
    ArrayList<ShoppingCart> cart_list;
    Bundle bundle;
    String username_session;
    PurchaseAdapter adapter;
    Button confirmbtn;
    DatabaseReference ref_for_status;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);



        trans_id = findViewById(R.id.transaction_detail_id);
        trans_date = findViewById(R.id.transaction_detail_date);
        trans_status = findViewById(R.id.transaction_detail_status);
        trans_courier = findViewById(R.id.transaction_detail_courier);
        trans_cost = findViewById(R.id.transaction_detail_cost);
        trans_bank = findViewById(R.id.transaction_detail_method);
        trans_item = findViewById(R.id.transaction_detail_item);
        recyclerView = findViewById(R.id.transaction_detail_recycler);
        pay_btn = findViewById(R.id.transaction_detail_pay_btn);
        confirmbtn = findViewById(R.id.transaction_detail_confirm_shipping_btn);

        bundle = getIntent().getExtras();
        username_session = bundle.getString("username");
        Log.d("trans_det", "username: " + bundle.getString("username"));
        Log.d("trans_det", "id " + bundle.getString("trans_id"));
        Log.d("trans_det", "date " + bundle.getString("date"));
        Log.d("trans_det", "status " + bundle.getString("status"));
        Log.d("trans_det", "courier: " + bundle.getString("courier"));
        Log.d("trans_det", "method: " + bundle.getString("method"));
        Log.d("trans_det", "cost: " + bundle.getInt("price"));
        Log.d("trans_det", "item: " + bundle.getInt("item"));

        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionDetailActivity.this);
        builder.setMessage("Are you sure you already receive this item(s)?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ref_for_status = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("transaction").child(bundle.getString("trans_id")).child("status");
                        ref_for_status.setValue("Finished");
                        trans_status.setText("Finished");
                        confirmbtn.setVisibility(View.GONE);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        final AlertDialog alert = builder.create();

        setTitle(bundle.getString("trans_id"));

        if(bundle.getString("status").equals("Waiting Payment")){
            pay_btn.setVisibility(View.VISIBLE);
            pay_btn.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
        else {
            pay_btn.setVisibility(View.GONE);
        }
        if(bundle.getString("status").equals("Shipping")){
            confirmbtn.setVisibility(View.VISIBLE);
        }
        else {
            confirmbtn.setVisibility(View.GONE);
        }
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransactionDetailActivity.this,PaymentDetailActivity.class);
                intent.putExtra("username",username_session);
                intent.putExtra("total_cost",bundle.getInt("price"));
                intent.putExtra("payment_method",bundle.getString("method"));
                intent.putExtra("trans_id",bundle.getString("trans_id"));
                startActivity(intent);

            }
        });
        cart_list = bundle.getParcelableArrayList("cart");
        trans_id.setText(bundle.getString("trans_id"));
        trans_date.setText(bundle.getString("date"));
        trans_status.setText(bundle.getString("status"));
        trans_courier.setText(bundle.getString("courier"));
        trans_bank.setText(bundle.getString("method"));
        trans_cost.setText("$"+Integer.toString(bundle.getInt("price")) + ".00");
        trans_item.setText(Integer.toString(bundle.getInt("item")) + " item(s)");

        ref_for_status = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("transaction").child(bundle.getString("trans_id")).child("status");
        ref_for_status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue().toString();
                trans_status.setText(status);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adapter = new PurchaseAdapter(cart_list,username_session);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TransactionDetailActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }
}
