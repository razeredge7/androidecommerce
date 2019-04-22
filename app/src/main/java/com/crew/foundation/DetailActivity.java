package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetailActivity extends AppCompatActivity {

    CarouselView carouselView;
    TextView title;
    TextView subtitle;
    DatabaseReference mDatabase;



    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //not yet
//        switch (item.getItemId()){
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//
//                default:
//                    return super.onOptionsItemSelected(item);
//
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        carouselView = findViewById(R.id.product_carousel);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);


        // tes firebase get data
        title = findViewById(R.id.product_title);
        subtitle = findViewById(R.id.product_subtitle);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("THIS", "masuk");
                String username = dataSnapshot.child("users").child("0").child("username").getValue(String.class);
                String password = dataSnapshot.child("users").child("0").child("password").getValue(String.class);
                Log.d("THIS", username);
                title.setText(username);
                subtitle.setText(password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }

    };
}
