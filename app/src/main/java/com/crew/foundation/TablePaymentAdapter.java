package com.crew.foundation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TablePaymentAdapter extends RecyclerView.Adapter<TablePaymentAdapter.TablePaymentViewHolder> {
    private ArrayList<PaymentView> datalist;
    Context context;
    DatabaseReference ref_for_status;

    public TablePaymentAdapter(ArrayList<PaymentView> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;
    }

    @NonNull
    @Override
    public TablePaymentAdapter.TablePaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_payment,parent,false);
        return  new TablePaymentAdapter.TablePaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TablePaymentAdapter.TablePaymentViewHolder holder, final int position) {

        int rowpos = holder.getAdapterPosition();
        if(rowpos == 0){

            holder.username.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.email.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.trans_id.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.trans_date.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.method.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.confirm.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.confirm.setTextColor(Color.parseColor("#000000"));
            holder.cost.setBackgroundResource(R.drawable.rect_for_table_head);

            holder.username.setText("Username");
            holder.trans_date.setText("Trasaction Date");
            holder.email.setText("Email");
            holder.trans_id.setText("Trasaction ID");
            holder.method.setText("Method");
            holder.confirm.setText("Confirm");
            holder.cost.setText("Cost");
        }
        else {
            final PaymentView paymentView = datalist.get(rowpos - 1);
            holder.username.setText(paymentView.getUsername());
            holder.email.setText(paymentView.getEmail());
            holder.trans_id.setText(paymentView.getTrans_id());
            holder.trans_date.setText(paymentView.getTrans_date());
            final String tes = paymentView.getTrans_id();
            holder.method.setText(paymentView.getMethod());
            holder.cost.setText("$"+paymentView.getCost() + ".00");
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            ((LinearLayout.LayoutParams) params).setMargins(5,5,5,5);
            holder.confirm.setLayoutParams(params);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Confirm this payment??")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ref_for_status = FirebaseDatabase.getInstance().getReference().child("users").child(paymentView.getUsername()).child("transaction").child(paymentView.getTrans_id());
                            ref_for_status.child("status").setValue("Shipping");
                            datalist.remove(position - 1);
                            notifyItemRemoved(position - 1);
                            notifyItemRangeChanged(position - 1,datalist.size());
                            notifyDataSetChanged();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
            });
            final AlertDialog alert = builder.create();

            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("paymentcheck", "onClick: " + tes);
                    alert.show();
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return datalist.size() + 1;
    }

    public class TablePaymentViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private TextView trans_date;
        private TextView email;
        private TextView trans_id;
        private TextView method;
        private TextView cost;
        private Button confirm;

        public TablePaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.row_payment_username);
            trans_date = itemView.findViewById(R.id.row_payment_trans_date);
            email = itemView.findViewById(R.id.row_payment_email);
            trans_id = itemView.findViewById(R.id.row_payment_trans_id);
            cost = itemView.findViewById(R.id.row_payment_price);
            method = itemView.findViewById(R.id.row_payment_method);
            confirm = itemView.findViewById(R.id.row_payment_conffirm_button);
        }
    }
}
