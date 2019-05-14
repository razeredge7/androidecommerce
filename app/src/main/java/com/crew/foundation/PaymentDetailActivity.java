package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentDetailActivity extends AppCompatActivity {

    Bundle bundle;
    TextView total_price,bank_name,bank_account_num,bank_account_name;
    String username_session;
    Button confirm_btn;
    DatabaseReference ref_for_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);

        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentDetailActivity.this);
        builder.setMessage("Thank you for paying, We will confirm your payment as soon as possible")
                .setCancelable(false)
                .setPositiveButton(" Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(PaymentDetailActivity.this,MainActivity.class);
                        intent.putExtra("username",username_session);
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();

        bundle = getIntent().getExtras();
        total_price = findViewById(R.id.payment_detail_price);
        bank_name = findViewById(R.id.payment_detail_bank);
        bank_account_num = findViewById(R.id.payment_detail_rek_num);
        bank_account_name = findViewById(R.id.payment_detail_rek_name);
        confirm_btn = findViewById(R.id.payment_detail_btn);
        username_session = bundle.getString("username");
        String total = Integer.toString(bundle.getInt("total_cost"));
        String bank = bundle.getString("payment_method");
        final String trans_id = bundle.getString("trans_id");
        setTitle(trans_id);
        total_price.setText("$" + total + ".00");
        if (bank.equals("Bank Transfer - Mandiri")){
            bank_name.setText("Bank Mandiri");
            bank_account_num.setText("1560071117842");
            bank_account_name.setText("Ignatius Yudhistira");
        }
        else  if (bank.equals("Bank Transfer - BCA")){
            bank_name.setText("Bank BCA");
            bank_account_num.setText("6030378935");
            bank_account_name.setText("Ignatius Yudhistira");
        }
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref_for_status = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("transaction").child(trans_id).child("status");
                ref_for_status.setValue("Waiting Confirmation");
                alert.show();
            }
        });

    }
}
