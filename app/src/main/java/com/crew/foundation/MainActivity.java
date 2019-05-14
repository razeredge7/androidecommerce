package com.crew.foundation;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    String username_session;
    Bundle bundle1 = new Bundle();
    AlertDialog alert;

    //ganti action bar title per fragment
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_title,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_favorite:
                if(username_session.equals("guest")){
                    alert.show();
                }else {
                    Intent intent = new Intent(MainActivity.this,WishlistActivity.class);
                    intent.putExtra("username",username_session);
                    startActivity(intent);
                }
                return true;
            case R.id.menu_shopping_cart:
                if(username_session.equals("guest")){
                    alert.show();
                }else {
                    Intent intent1 = new Intent(MainActivity.this,ShoppingCartActivity.class);
                    intent1.putExtra("username",username_session);
                    startActivity(intent1);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("You are logged as guest, please sign up or login")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Sign up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, signupactivity.class);
                        startActivity(intent);
                    }
        });
        alert = builder.create();

        //ambil username dari login page
        Bundle bundle = getIntent().getExtras();

        username_session = bundle.getString("username");
        Log.d("tes", "onCreate: " + username_session);

        bundle1.putString("username",username_session);

        // kita set default nya Home Fragment
        loadFragment(new HomeFragment());

        // inisialisasi BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_main);
        // beri listener pada saat item/menu bottomnavigation terpilih
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    // method untuk load fragment yang sesuai
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            fragment.setArguments(bundle1);
            return true;
        }
        return false;
    }

    // method listener untuk logika pemilihan
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        //buat bundle baru buat pass username ke fragment
        Log.d("tes", "onNavigationItemSelected:1 " + bundle1.getString("username"));
        switch (item.getItemId()){
            case R.id.home_menu:
                Log.d("tes", "onNavigationItemSelected:2 " + bundle1.getString("username"));
                fragment = new HomeFragment();
                //pasang bundle ke fragment
                fragment.setArguments(bundle1);
                break;
            case R.id.transaction_menu:
                if(username_session.equals("guest")){
                    alert.show();
                }else {
                    fragment = new TransactionFragment();
                    fragment.setArguments(bundle1);
                }
                break;
            case R.id.account_menu:
                if(username_session.equals("guest")){
                    alert.show();
                }else {
                    fragment = new AccountFragment();
                    //pasang bundle ke fragment
                    fragment.setArguments(bundle1);
                }
                break;
        }
        return loadFragment(fragment);
    }
}