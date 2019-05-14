package com.crew.foundation;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TransactionFragment extends Fragment {

    RecyclerView recyclerView;
    TextView no_trans;
    ProgressBar progressBar;
    ArrayList<Transaction> transactionArrayList;
    TransactionAdapter adapter;
    private Fragment fragment = this;
    private DatabaseReference ref_for_trans;
    private Handler WaitHandler = new Handler();


    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        ((MainActivity) getActivity())
                .setActionBarTitle("Transaction");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.transaction_loading);
        no_trans = view.findViewById(R.id.no_transaction);
        no_trans.setVisibility(View.GONE);

        final String username_session = getArguments().getString("username");

        ref_for_trans = FirebaseDatabase.getInstance().getReference().child("users").child(username_session).child("transaction");
        ref_for_trans.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                transactionArrayList = new ArrayList<>();
                progressBar.setVisibility(View.VISIBLE);
                long childCount = dataSnapshot.getChildrenCount();
                if(childCount > 1){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren() ) {
                        if(!snapshot.child("transaction_Id").getValue().equals("")){
                            Log.d("trans", "onDataChange: 1 " + snapshot.getValue());
                            Transaction transaction = snapshot.getValue(Transaction.class);
                            transactionArrayList.add(transaction);
                        }
                    }
                    no_trans.setVisibility(View.GONE);
                    recyclerView = view.findViewById(R.id.transaction_recycler);
                    Log.d("trans", "onCreate:  jalan");
                    adapter = new TransactionAdapter(transactionArrayList,username_session);
                    Log.d("trans", "onCreate:  jalan 2");
                    //buat jadi 2 grid
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                }
                else{
                    no_trans.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
}