package com.crew.foundation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private ArrayList<Transaction> datalist;
    private String username_session;
    DatabaseReference ref_for_trans;

    public TransactionAdapter(ArrayList<Transaction> datalist, String username_session) {
        this.datalist = datalist;
        this.username_session = username_session;
    }

    @NonNull
    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_transaction,parent,false);
        return  new TransactionAdapter.TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransactionAdapter.TransactionViewHolder holder, int position) {
        holder.status.setText(datalist.get(position).getStatus());
        holder.cost.setText("$" + Integer.toString(datalist.get(position).getTotal_cost()) + ".00");
        holder.date.setText(datalist.get(position).getDate());
        holder.id.setText(datalist.get(position).getTransaction_Id());
        final Intent intent = new Intent(holder.cv.getContext(),TransactionDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username",username_session);
        bundle.putParcelableArrayList("cart",datalist.get(position).getCart());
        bundle.putString("trans_id",datalist.get(position).getTransaction_Id());
        bundle.putString("date",datalist.get(position).getDate());
        bundle.putString("status",datalist.get(position).getStatus());
        bundle.putString("courier",datalist.get(position).getCourier());
        bundle.putInt("price",datalist.get(position).getTotal_cost());
        bundle.putInt("item",datalist.get(position).getTotal_item());
        bundle.putString("method",datalist.get(position).getPayment());
        intent.putExtras(bundle);
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

    public class TransactionViewHolder extends RecyclerView.ViewHolder{
        private TextView status;
        private TextView date;
        private TextView id;
        private TextView cost;
        private CardView cv;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.row_transaction_status);
            date = itemView.findViewById(R.id.row_transaction_date);
            id = itemView.findViewById(R.id.row_transaction_id);
            cost = itemView.findViewById(R.id.row_transaction_price);
            cv = itemView.findViewById(R.id.row_transaction_card);
        }
    }
}
