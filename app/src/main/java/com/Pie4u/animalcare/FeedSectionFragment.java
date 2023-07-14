package com.Pie4u.animalcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FeedSectionFragment extends Fragment {

    private ArrayList<BlogData> blogDataArrayList;
    private ArrayList<BlogData> tempList;
    private CustomListViewAdapter customListViewAdapter;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String ImageUrlsList;
    private ProgressDialog progressDialog;

    public FeedSectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_section, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating the Feed....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseAuth =FirebaseAuth.getInstance();
        updateTheData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Add a new Post", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getContext(),CreatePostActivity.class);
                startActivity(i);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setSize(0);
                Toast.makeText(getContext(), "Feed Refreshed", Toast.LENGTH_LONG).show();
               updateTheData();
               swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    public void updateTheData(){
        firebaseFirestore.collection("BlogData")
                .orderBy("blogTimeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null || value==null){
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    //Toast.makeText(FeedSectionFragment.this.getContext(), "Error occurred while loading feed", Toast.LENGTH_SHORT).show();
                }
                else if(!value.isEmpty()){
                    blogDataArrayList = new ArrayList<>();
                    for(DocumentSnapshot documentSnapshot : value){
                        blogDataArrayList.add(documentSnapshot.toObject(BlogData.class));
                    }
                    setUpListView();
                }else{
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    Log.e("TAG","onEvent 3: " );
                }
            }
        });
    }

    public void setUpListView(){
        customListViewAdapter = new CustomListViewAdapter(requireContext(),blogDataArrayList);
        listView.setAdapter(customListViewAdapter);
        customListViewAdapter.notifyDataSetChanged();

        if(progressDialog.isShowing())
            progressDialog.cancel();
    }

}
