package com.crew.foundation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {
    private ArrayList<ShoppingCart> datalist;
    private String username_session;
    Context context;
    StorageReference ref_for_product_pic;
    DatabaseReference ref_for_cart;


    public ShoppingAdapter(ArrayList<ShoppingCart> datalist, String username_session, Context context) {
        this.context = context;
        this.datalist = datalist;
        this.username_session = username_session;
    }
    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_shopping,parent,false);
        return  new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShoppingViewHolder holder, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete this item?")
                .setCancelable(false)
                .setPositiveButton("Delete item", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ref_for_cart = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("cart");
                        ref_for_cart.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String id_name = datalist.get(position).getId_name();
                                ref_for_cart.child(id_name).setValue(null);
                                datalist.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,datalist.size());
                                notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
                });
        final AlertDialog alert = builder.create();
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
        holder.category.setText(datalist.get(position).getCategory());
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return (datalist != null) ? datalist.size() : 0;
    }

    public int getTotalPrice(){
        int total = 0;
        for(int i = 0; i < datalist.size(); i++){
            int temp = datalist.get(i).getPrice();
            total += temp;
        }
        return total;
    }

    public ArrayList<ShoppingCart> getCurrentList() {
        return datalist;
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView category;
        private TextView price;
        public CardView cv;
        private ImageView iv;
        public ImageButton close;

        public ShoppingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.row_transaction_id);
            category = itemView.findViewById(R.id.row_transaction_date);
            price = itemView.findViewById(R.id.row_shopping_price);
            cv = itemView.findViewById(R.id.row_shopping_card);
            iv = itemView.findViewById(R.id.row_shopping_image);
            close = itemView.findViewById(R.id.row_shopping_close);

        }
    }

}
