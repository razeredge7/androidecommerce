package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShoppingCartActivity extends AppCompatActivity {

    DatabaseReference ref_for_cart;
    int total_item_count;
    int total_item_price;
    public TextView empty_cart,total_item,total_price;
    private RecyclerView recyclerView;
    private ArrayList<ShoppingCart> cartArrayList, final_cart_list;
    private ShoppingAdapter adapter;
    private Handler WaitHandler = new Handler();
    ProgressBar progressBar;
    Button checkoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        setTitle("Shopping Cart");
        progressBar = findViewById(R.id.cart_loading);
        progressBar.setVisibility(View.VISIBLE);
        final Bundle bundle = getIntent().getExtras();
        final String username_session = bundle.getString("username");
        empty_cart = findViewById(R.id.cart_empty);
        empty_cart.setVisibility(View.GONE);
        total_item = findViewById(R.id.cart_total_item);
        total_price = findViewById(R.id.cart_total_price);
        checkoutbtn = findViewById(R.id.cart_checkout_button);

        cartArrayList = new ArrayList<>();
        final_cart_list = new ArrayList<>();

        ref_for_cart = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("cart");
        ref_for_cart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childCount = dataSnapshot.getChildrenCount();
                if(childCount > 1){
                    empty_cart.setVisibility(View.GONE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                        if(!snapshot.child("title").getValue().equals("")){
                            Log.d("cart", "onDataChange: 1 " + snapshot.getValue());
                            ShoppingCart cartlist = snapshot.getValue(ShoppingCart.class);
                            cartArrayList.add(cartlist);
                        }
                    }
                }
                else{
                    empty_cart.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        WaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    recyclerView = findViewById(R.id.cart_recycler);
                    Log.d("wishlist", "onCreate:  jalan");
                    adapter = new ShoppingAdapter(cartArrayList,username_session,ShoppingCartActivity.this);
                    Log.d("wishlist", "onCreate:  jalan 2");
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ShoppingCartActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    ref_for_cart = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("cart");
                    ref_for_cart.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() <=1){
                                empty_cart.setVisibility(View.VISIBLE);
                            }
                            else{
                                empty_cart.setVisibility(View.GONE);
                            }
                            total_item_count = adapter.getItemCount();
                            total_item.setText(Integer.toString(total_item_count) + " item(s)");
                            total_item_price = adapter.getTotalPrice();
                            total_price.setText("$" +Integer.toString(total_item_price) + ".00");
                            final_cart_list = adapter.getCurrentList();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    checkoutbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(adapter.getItemCount() == 0){
                                Toast.makeText(getApplicationContext(),"Shopping Cart is empty",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Bundle bundle1 = new Bundle();
                                bundle1.putParcelableArrayList("shopping_list",final_cart_list);
                                bundle1.putInt("total_item",total_item_count);
                                bundle1.putInt("total_price",total_item_price);
                                bundle1.putString("username",username_session);
                                Intent intent = new Intent(ShoppingCartActivity.this,PaymentActivity.class);
                                intent.putExtras(bundle1);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },2000);






    }

}
