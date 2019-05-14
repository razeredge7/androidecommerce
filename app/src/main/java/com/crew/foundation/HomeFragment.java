package com.crew.foundation;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

public class HomeFragment extends Fragment {

    private Fragment fragment = this;
    private String[] categoryArray = new String[4];
    private DatabaseReference ref_for_category;
    private ListView listView;
    private CarouselView carouselView;
    private Handler WaitHandler = new Handler();
    private int[] sampleImages = {R.drawable.notebook, R.drawable.consoles, R.drawable.smartphone};
    private ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    ProgressBar progressBar;

    //biar listview bisa muncul di scroll view
    public void justifyListViewHeightBasedOnChildren (ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //ganti title action bar
        ((MainActivity) getActivity())
                .setActionBarTitle("Welcome");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.home_loading);
        progressBar.setVisibility(View.VISIBLE);
        final String username_session = getArguments().getString("username");
        Log.d("tes", "onViewCreated: " + username_session);
        ref_for_category = FirebaseDatabase.getInstance().getReference().child("product");
        ref_for_category.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren() ){
                    String addCategory = snapshot.getKey();
                    Log.d("category", "onDataChange: " + addCategory);
                    categoryArray[i] = addCategory;
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        WaitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    carouselView = view.findViewById(R.id.home_product_carousel);
                    carouselView.setImageListener(imageListener);
                    carouselView.setPageCount(sampleImages.length);

                    carouselView.setImageClickListener(new ImageClickListener() {
                        @Override
                        public void onClick(int position) {
                            if(position == 0){
                                Intent intent = new Intent(getActivity(),ViewProductActivity.class);
                                intent.putExtra("category","Notebook");
                                intent.putExtra("username",username_session);
                                startActivity(intent);
                            }
                            else if(position == 1){
                                Intent intent = new Intent(getActivity(),ViewProductActivity.class);
                                intent.putExtra("category","Game Console");
                                intent.putExtra("username",username_session);
                                startActivity(intent);

                            }
                            else if(position == 2){
                                Intent intent = new Intent(getActivity(),ViewProductActivity.class);
                                intent.putExtra("category","Smartphone");
                                intent.putExtra("username",username_session);
                                startActivity(intent);

                            }
                        }
                    });

                    ArrayAdapter adapter = new ArrayAdapter(getActivity(),R.layout.row_category, categoryArray);
                    listView = view.findViewById(R.id.home_list_category);
                    listView.setAdapter(adapter);
                    justifyListViewHeightBasedOnChildren(listView);

                    //pilih bedasarkan category yang diclick
                    progressBar.setVisibility(View.GONE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            Object listitem = listView.getItemAtPosition(position);
                            Intent intent = new Intent(getActivity(),ViewProductActivity.class);
                            intent.putExtra("category",listitem.toString());
                            intent.putExtra("username",username_session);
                            Log.d("tesclick", "onItemClick: " + listitem.toString());
                            startActivity(intent);
                        }
                    });
                }
                catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        },3000);







    }



}

