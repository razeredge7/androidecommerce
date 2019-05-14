package com.crew.foundation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {

    String courierTitle="";
    int courierPrice=0;
    String bankMethod="";
    Bundle bundle;
    Button BtnNext;

    public void onCourierGroupClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.payment_courier_reg:
                if (checked)
                    courierTitle = "Reguler (2 - 5 days)";
                    courierPrice = 5;
                    bundle.putString("courier_type",courierTitle);
                    bundle.putInt("courier_price",courierPrice);
                    RadioButton couriernext = findViewById(R.id.payment_courier_next);
                    couriernext.setChecked(false);

                    break;
            case R.id.payment_courier_next:
                if (checked)
                    courierTitle = "Next Day (1 day)";
                    courierPrice = 10;
                    bundle.putString("courier_type",courierTitle);
                    bundle.putInt("courier_price",courierPrice);
                    RadioButton courierreg = findViewById(R.id.payment_courier_reg);
                    courierreg.setChecked(false);
                    break;
        }
    }

    public void onPaymentGroupClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.payment_bank_mandiri:
                if (checked)
                    bankMethod = "Bank Transfer - Mandiri";
                    bundle.putString("payment_method",bankMethod);
                    RadioButton bca = findViewById(R.id.payment_bank_bca);
                    bca.setChecked(false);
                    break;
            case R.id.payment_bank_bca:
                if (checked)
                    bankMethod = "Bank Transfer - BCA";
                    bundle.putString("payment_method",bankMethod);
                    RadioButton mandiri = findViewById(R.id.payment_bank_mandiri);
                    mandiri.setChecked(false);
                    break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setTitle("Payment & Courier");
        bundle = getIntent().getExtras();
        BtnNext = findViewById(R.id.payment_btn);
        Log.d("payment", "total item" + bundle.getInt("total_item"));
        Log.d("payment", "total price" + bundle.getInt("total_price"));
        Log.d("payment", "username " + bundle.getString("username"));
        Log.d("payment", "datalist" + bundle.getParcelableArrayList("shopping_list"));


        BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("payment", "courier " + bundle.getString("courier_type"));
                Log.d("payment", "bank " + bundle.getString("payment_method"));
                if (courierTitle.equals("") || bankMethod.equals("")){
                    Toast.makeText(PaymentActivity.this, "Please choose your preferred courier and payment method", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(PaymentActivity.this,PurchaseConfirmActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }


            }
        });
    }
}
