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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Products> productsArrayList;
    private ArrayList<String> wishlistArrayList;
    private ProductAdapter adapter;
    private Handler WaitHandler = new Handler();
    TextView no_wishlist;
    DatabaseReference ref_for_wishlist,ref_for_products;
    ProgressBar progressBar;
    String username_session;


    @Override
    protected void onResume() {
        super.onResume();
        ref_for_products = FirebaseDatabase.getInstance().getReference().child("product");
        ref_for_products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("tesman", "onDataChange: 2");
                ArrayList<Products> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        Iterator<String> itr = wishlistArrayList.iterator();
                        while(itr.hasNext()){
                            String check_id_name = itr.next();
                            Log.d("wishlist", "onDataChange: itr:" + check_id_name);
                            if(check_id_name.equals(snapshot1.child("id_name").getValue())){
                                Log.d("wishlist", "onDataChange: yg masuk : " + snapshot1.child("id_name").getValue());
                                Products products = snapshot1.getValue(Products.class);
                                data.add(products);
                            }
                        }
                    }
                }
                productsArrayList = data;
                Log.d("wishlist", "onDataChange: finish loop 2");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        WaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("tesman", "onResume: ");
                    recyclerView = findViewById(R.id.wishlist_recycler);
                    Log.d("wishlist", "onCreate:  jalan");
                    adapter = new ProductAdapter(productsArrayList,username_session);
                    Log.d("wishlist", "onCreate:  jalan 2");
                    //buat jadi 2 grid
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(WishlistActivity.this,2);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        setTitle("Wishlist");
        no_wishlist = findViewById(R.id.wishlist_check);
        no_wishlist.setVisibility(View.GONE);
        progressBar = findViewById(R.id.wishlist_loading);
        Bundle bundle = getIntent().getExtras();
        username_session = bundle.getString("username");
        Log.d("tes", "view product onCreate username: " + username_session);
        ref_for_wishlist = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("wishlist");
        ref_for_wishlist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishlistArrayList = new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);
                Log.d("wishlist", "onDataChange: childcount: " + dataSnapshot.getChildrenCount());
                Log.d("tesman", "onDataChange: 1");
                long childCount = dataSnapshot.getChildrenCount();
                if(childCount > 1){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                        if(!snapshot.getValue().equals("")){
                            Log.d("wishlist", "onDataChange: 1 " + snapshot.getValue());
                            String wishlist = snapshot.getValue().toString();
                            wishlistArrayList.add(wishlist);
                        }
                    }
                    no_wishlist.setVisibility(View.GONE);
                }
                else{
                    no_wishlist.setVisibility(View.VISIBLE);
                }
                Log.d("wishlist", "onDataChange: finish loop 1");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        ref_for_products = FirebaseDatabase.getInstance().getReference().child("product");
        ref_for_products.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Products> data = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        Iterator<String> itr = wishlistArrayList.iterator();
                        while(itr.hasNext()){
                            String check_id_name = itr.next();
                            Log.d("wishlist", "onDataChange: itr:" + check_id_name);
                            if(check_id_name.equals(snapshot1.child("id_name").getValue())){
                                Log.d("wishlist", "onDataChange: yg masuk : " + snapshot1.child("id_name").getValue());
                                Products products = snapshot1.getValue(Products.class);
                                data.add(products);
                            }
                        }
                    }
                }
                productsArrayList = data;
                Log.d("wishlist", "onDataChange: finish loop 2");
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
                    Log.d("wishlist", "onCreate:  jalan");
                    adapter = new ProductAdapter(productsArrayList,username_session);
                    Log.d("wishlist", "onCreate:  jalan 2");
                    //buat jadi 2 grid
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(WishlistActivity.this,2);
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
