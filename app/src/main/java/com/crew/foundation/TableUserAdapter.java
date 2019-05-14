package com.crew.foundation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TableUserAdapter extends RecyclerView.Adapter<TableUserAdapter.TableUserViewHolder> {
    private ArrayList<UserView> datalist;

    public TableUserAdapter(ArrayList<UserView> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public TableUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_users,parent,false);
        return  new TableUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableUserViewHolder holder, int position) {

        int rowpos = holder.getAdapterPosition();
        if(rowpos == 0){
            holder.username.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.fullname.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.regisdate.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.email.setBackgroundResource(R.drawable.rect_for_table_head);
            holder.address.setBackgroundResource(R.drawable.rect_for_table_head);

            holder.username.setText("Username");
            holder.fullname.setText("Fullname");
            holder.regisdate.setText("Register Date");
            holder.email.setText("Email");
            holder.address.setText("Address");
        }
        else {

            UserView userView = datalist.get(rowpos - 1);

            holder.username.setText(userView.getUsername());
            holder.fullname.setText(userView.getFirst_name() + " " + userView.getLast_name());
            holder.regisdate.setText(userView.getRegisDate());
            holder.email.setText(userView.getEmail());
            holder.address.setText(userView.getAddress());
        }

    }

    @Override
    public int getItemCount() {
        return datalist.size() + 1;
}

    public class TableUserViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private TextView fullname;
        private TextView regisdate;
        private TextView email;
        private TextView address;

        public TableUserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.row_user_username);
            fullname = itemView.findViewById(R.id.row_user_fullname);
            regisdate = itemView.findViewById(R.id.row_user_regisdate);
            email = itemView.findViewById(R.id.row_user_email);
            address = itemView.findViewById(R.id.row_user_address);
        }
    }
}
