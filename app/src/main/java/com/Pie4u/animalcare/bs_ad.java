package com.Pie4u.animalcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class bs_ad extends AppCompatActivity {

    Button up_btn;
    RecyclerView rc_view;
    ArrayList<ProductModel> recycleList;

    FirebaseDatabase database;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs_ad);

        up_btn=findViewById(R.id.up_btn);
        rc_view=findViewById(R.id.rc_view);
        recycleList = new ArrayList<>();

        database=FirebaseDatabase.getInstance("https://animalcare-b79a9-default-rtdb.firebaseio.com/");
        final ProjectAdapter recyclerAdapter =  new ProjectAdapter(recycleList,getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rc_view.setLayoutManager(linearLayoutManager);
        rc_view.addItemDecoration(new DividerItemDecoration(rc_view.getContext(),DividerItemDecoration.VERTICAL));
        rc_view.setNestedScrollingEnabled(false);
        rc_view.setAdapter(recyclerAdapter);

        database.getReference().child("pet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ProductModel productModel=dataSnapshot.getValue(ProductModel.class);
                    recycleList.add(productModel);
                }
                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(bs_ad.this,create_ad.class);
                startActivity(i);

            }
        });

    }
}