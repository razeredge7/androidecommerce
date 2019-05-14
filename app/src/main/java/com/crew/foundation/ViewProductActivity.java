package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Products> productsArrayList;
    private ProductAdapter adapter;
    private Handler WaitHandler = new Handler();
    DatabaseReference ref_for_products;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        progressBar = findViewById(R.id.view_loading);
        Bundle bundle = getIntent().getExtras();
        final String category = bundle.getString("category");
        setTitle(category);
        final String username_session = bundle.getString("username");
        Log.d("tes", "view product onCreate category: " + category);
        Log.d("tes", "view product onCreate username: " + username_session);
        ref_for_products = FirebaseDatabase.getInstance().getReference().child("product").child(category);
        ref_for_products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                ArrayList<Products> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    Products products = snapshot.getValue(Products.class);
                    data.add(products);
                }
                productsArrayList = data;
                Log.d("tes", "onDataChange:  jalan");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        WaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    recyclerView = findViewById(R.id.wishlist_recycler);
                    Log.d("tes", "onCreate:  jalan");
                    adapter = new ProductAdapter(productsArrayList,username_session);
                    Log.d("tes", "onCreate:  jalan 2");
                    //buat jadi 2 grid
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ViewProductActivity.this,2);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        },2000);
    }
}
