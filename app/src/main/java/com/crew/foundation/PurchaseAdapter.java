package com.crew.foundation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {
    private ArrayList<ShoppingCart> datalist;
    private String username_session;
    StorageReference ref_for_product_pic;
    DatabaseReference ref_for_cart;

    public PurchaseAdapter(ArrayList<ShoppingCart> datalist, String username_session) {
        this.datalist = datalist;
        this.username_session = username_session;
    }

    @NonNull
    @Override
    public PurchaseAdapter.PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_purchase,parent,false);
        return  new PurchaseAdapter.PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PurchaseAdapter.PurchaseViewHolder holder, int position) {
        String category_string = datalist.get(position).getCategory();
        String category_path = null;
        if(category_string.equals("Game Console")){
            category_path = "game_console_";
        }
        else if(category_string.equals("Home Appliance")){
            category_path = "home_appliance_";
        }
        else if(category_string.equals("Notebook")){
            category_path = "notebook_";
        }
        else if(category_string.equals("Smartphone")){
            category_path = "smartphone_";
        }
        ref_for_product_pic = FirebaseStorage.getInstance().getReferenceFromUrl("gs://uas-ecommerce-c3a79.appspot.com")
                .child("product_images").child(category_path + "images").child(datalist.get(position).getPic_path()+".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        String tes = ref_for_product_pic.toString();
        Log.d("pic", "masuk: " + tes);
        ref_for_product_pic.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d("pic", "masuk: 2");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.iv.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("pic", "onFailure: ", e);
            }
        });
        holder.title.setText(datalist.get(position).getTitle());
        holder.price.setText("$ " + Integer.toString(datalist.get(position).getPrice()) + ".00");

    }

    @Override
    public int getItemCount() {
        return (datalist != null) ? datalist.size() : 0;
    }

    public class PurchaseViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView price;
        private ImageView iv;


        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.row_purchase_title);
            price = itemView.findViewById(R.id.row_purchase_price);
            iv = itemView.findViewById(R.id.row_purchase_image);
        }
    }
}
