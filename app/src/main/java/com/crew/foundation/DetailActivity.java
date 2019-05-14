package com.crew.foundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {


    ImageView product_image;
    TextView title,category,price,stock,desc;
    StorageReference ref_for_product_pic;
    DatabaseReference ref_for_wishlist,ref_for_cart;
    Button order_btn;
    ImageButton fav_btn,share_btn;
    String product_name,product_category,product_short_name,product_desc,product_pic_path,category_path,product_id_name,username_session;
    int product_price,product_stock;
    private Handler WaitHandler = new Handler();
    final boolean[] check_wishlist = new boolean[1];
    ProgressBar progressBar;
    AlertDialog alert;
    Bitmap imageToFb;
    private CallbackManager callbackManager;
    ShareDialog shareDialog;


    private void is_wishlisted() {// true jika telah di wishlist, return false jika belom
        //check udh di wishlist atau blom
        //final boolean[] check_wishlist = new boolean[1];
        ref_for_wishlist = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("wishlist");
        ref_for_wishlist.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue().toString().equals(product_id_name)) {
                            Log.d("check_wishlist", "onDataChange: " + "true");
                            check_wishlist[0] = true;
                            break;
                        } else {
                            Log.d("check_wishlist", "onDataChange: " + "false");
                            check_wishlist[0] = false;
                        }
                    }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
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
                }
                else {
                    Intent intent = new Intent(DetailActivity.this,WishlistActivity.class);
                    intent.putExtra("username",username_session);
                    startActivity(intent);
                }

                return true;
            case R.id.menu_shopping_cart:
                if(username_session.equals("guest")){
                    alert.show();
                }
                else {
                    Intent intent1 = new Intent(DetailActivity.this,ShoppingCartActivity.class);
                    intent1.putExtra("username",username_session);
                    startActivity(intent1);
                }
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar = findViewById(R.id.detail_loading);
        progressBar.setVisibility(View.VISIBLE);
        fav_btn = findViewById(R.id.detail_favorite_button);
        share_btn = findViewById(R.id.detail_favorite_button);


        is_wishlisted();
        WaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d("check_wishlist", "onCreate:2 " + check_wishlist[0]);
                    if(check_wishlist[0]){
                        fav_btn.setImageResource(R.drawable.favorite);
                    }
                    else {
                        fav_btn.setImageResource(R.drawable.favorite_off);
                    }
                    progressBar.setVisibility(View.GONE);
                }
                catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        },2000);
    }

    public void onShareclicked(View view){
        Log.d("fbcheck", "onClick:  1");
        if(username_session.equals("guest")){
            alert.show();
        }
        else {
            Log.d("fbcheck", "onClick: 2 ");
            //fb_share_btn.callOnClick();
            Log.d("fbcheck", "onClick: ");
            shareDialog = new ShareDialog(DetailActivity.this);
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(imageToFb)
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .setShareHashtag(new ShareHashtag.Builder()
                            .setHashtag(product_name+"\n"+product_desc + "\n#CrewCommerce")
                            .build())
                    .build();
            shareDialog.show(content);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.v("checkfb", "Successfully posted");
            AlertDialog alertforFB;
            // Write some code to do some operations when you shared content successfully.
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setMessage("Upload Sucess!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //do nothing
                        }
                    });
            alertforFB = builder.create();
            alertforFB.show();
        }

        @Override
        public void onCancel() {
            Log.v("checkfb", "Sharing cancelled");
            // Write some code to do some operations when you cancel sharing content.
            AlertDialog alertforFB;
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setMessage("Upload Cancelled!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //do nothing
                        }
                    });
            alertforFB = builder.create();
            alertforFB.show();
        }

        @Override
        public void onError(FacebookException error) {
            Log.v("checkfb", error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
            final AlertDialog alertforFB;
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setMessage("Upload Failed!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //do nothing
                        }
                    });
            alertforFB = builder.create();
            alertforFB.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setMessage("You are logged as guest, please sign up or login")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("Sign up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(DetailActivity.this, signupactivity.class);
                startActivity(intent);
            }
        });
        alert = builder.create();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle bundle = getIntent().getExtras();
        setTitle("Detail");
        title = findViewById(R.id.detail_title);
        category = findViewById(R.id.detail_category);
        price = findViewById(R.id.detail_price);
        stock = findViewById(R.id.detail_stock);
        desc = findViewById(R.id.detail_desc);
        product_image = findViewById(R.id.detail_image_product);
        order_btn = findViewById(R.id.detail_order_button);
        fav_btn = findViewById(R.id.detail_favorite_button);
        share_btn = findViewById(R.id.detail_share_button);

        product_name = bundle.getString("product_name");
        product_short_name = bundle.getString("short_name");
        product_category = bundle.getString("category");
        product_desc = bundle.getString("description");
        product_pic_path =  bundle.getString("pic");
        product_price = bundle.getInt("price");
        product_stock = bundle.getInt("stock");
        category_path = bundle.getString("category_path");
        product_id_name = bundle.getString("id_name");
        username_session = bundle.getString("username");
        progressBar = findViewById(R.id.detail_loading);
        progressBar.setVisibility(View.VISIBLE);


        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();

        //register callback
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, callback);


        is_wishlisted();
        Log.d("check_wishlist", "onCreate:1 " + check_wishlist[0]);
        setTitle(product_short_name);
        Log.d("check", "detail: " + bundle.getString("category"));
        Log.d("check", "detail: " + bundle.getString("product_name") );
        Log.d("check", "detail: " + bundle.getString("short_name"));
        Log.d("check", "detail: " + bundle.getString("description"));
        Log.d("check", "detail: " + bundle.getString("pic"));
        Log.d("check", "detail: " + bundle.getInt("price"));
        Log.d("check", "detail: " + bundle.getInt("stock"));
        Log.d("check", "detail: " + bundle.getString("username"));
        Log.d("check", "detail: " + bundle.getString("id_name"));
        ref_for_product_pic = FirebaseStorage.getInstance().getReferenceFromUrl("gs://uas-ecommerce-c3a79.appspot.com")
                .child("product_images").child(category_path + "images").child(product_pic_path+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        String tes = ref_for_product_pic.toString();
        Log.d("pic", "masuk: " + tes);



        ref_for_product_pic.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("pic", "masuk: 2");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                product_image.setImageBitmap(bitmap);
                imageToFb = bitmap;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("pic", "onFailure: ", e);
            }
        });

        WaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.d("check_wishlist", "onCreate:2 " + check_wishlist[0]);
                    if(username_session.equals("guest")){
                        fav_btn.setImageResource(R.drawable.favorite_off);
                    }
                    else {
                        if(check_wishlist[0]){
                            fav_btn.setImageResource(R.drawable.favorite);
                        }
                        else {
                            fav_btn.setImageResource(R.drawable.favorite_off);
                        }
                    }
                    title.setText(product_name);
                    category.setText(product_category);
                    desc.setText(product_desc);
                    price.setText("$ " + Integer.toString(product_price) + ".00");
                    stock.setText(Integer.toString(product_stock));
                    progressBar.setVisibility(View.GONE);

                    fav_btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            if(username_session.equals("guest")){
                                alert.show();
                            }
                            else {
                                Log.d("check_wishlist", "onClick: " + check_wishlist[0]);
                                if(!check_wishlist[0]){
                                    Log.d("check_wishlist", "onClick: " + "not yet");
                                    Toast.makeText(getApplicationContext(),"Item added to wishlist",Toast.LENGTH_SHORT).show();
                                    ref_for_wishlist = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("wishlist");
                                    ref_for_wishlist.child(product_id_name).setValue(product_id_name);
                                    fav_btn.setImageResource(R.drawable.favorite);
                                    check_wishlist[0] = true;
                                }
                                else {
                                    Log.d("check_wishlist", "onClick: " + "already");
                                    Toast.makeText(getApplicationContext(),"Item removed from wishlist",Toast.LENGTH_SHORT).show();
                                    ref_for_wishlist = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("wishlist");
                                    ref_for_wishlist.child(product_id_name).setValue(null);
                                    fav_btn.setImageResource(R.drawable.favorite_off);
                                    check_wishlist[0] = false;
                                }
                            }
                        }
                    });
                    order_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(username_session.equals("guest")){
                                alert.show();
                            }
                            else {
                                if(stock.getText().toString().equals("0")){
                                    Toast.makeText(getApplicationContext(),"This item is sold out",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    ref_for_cart = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("cart");
                                    ref_for_cart.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean itemexist = false;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                                                Log.d("item", "onDataChange: " + snapshot.child("title").getValue() );
                                                if(snapshot.getKey().equals(product_id_name)){
                                                    Log.d("item", "onDataChange: item not exist" );
                                                    itemexist = true;
                                                    break;
                                                }
                                                else{
                                                    itemexist = false;
                                                }
                                            }
                                            if (!itemexist){
                                                ShoppingCart shoppingCart = new ShoppingCart(product_name,product_category,product_price,product_pic_path,product_id_name);
                                                ref_for_cart.child(product_id_name).setValue(shoppingCart);
                                                Toast.makeText(getApplicationContext(),"Item added to shopping cart",Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(),"Item already in shopping cart",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        },2000);



    }
}
