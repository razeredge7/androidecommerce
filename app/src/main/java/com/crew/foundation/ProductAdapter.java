package com.crew.foundation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<Products> datalist;
    private String username_session;
    StorageReference ref_for_product_pic;


    public ProductAdapter(ArrayList<Products> datalist,String username_session) {
        this.datalist = datalist;
        this.username_session = username_session;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_product,parent,false);
        return  new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
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
        Log.d("path", "onBindViewHolder: " + category_path);
        ref_for_product_pic = FirebaseStorage.getInstance().getReferenceFromUrl("gs://uas-ecommerce-c3a79.appspot.com")
               .child("product_images").child(category_path + "images").child(datalist.get(position).getPic()+".jpg");
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
        holder.Title.setText(datalist.get(position).getShort_name());
        holder.price.setText("$" + datalist.get(position).getPrice() + ".00");

        final Intent intent = new Intent(holder.cv.getContext(),DetailActivity.class);
        intent.putExtra("username",username_session);
        intent.putExtra("product_name",datalist.get(position).getProduct_name());
        intent.putExtra("short_name",datalist.get(position).getShort_name());
        intent.putExtra("description",datalist.get(position).getDescription());
        intent.putExtra("pic",datalist.get(position).getPic());
        intent.putExtra("price",datalist.get(position).getPrice());
        intent.putExtra("stock",datalist.get(position).getStock());
        intent.putExtra("id_name",datalist.get(position).getId_name());
        intent.putExtra("category", category_string);
        intent.putExtra("category_path",category_path);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cv.getContext().startActivity(intent);
                Log.d("pindahdetail", "onClick: sukses");
            }
        });
    }

    @Override
    public int getItemCount() {
        return (datalist != null) ? datalist.size() : 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        private TextView Title;
        public CardView cv;
        private ImageView iv;
        private  TextView price;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.row_product_title);
            cv = itemView.findViewById(R.id.row_product_card);
            iv = itemView.findViewById(R.id.row_product_image);
            price = itemView.findViewById(R.id.row_product_price);
        }
    }
}
