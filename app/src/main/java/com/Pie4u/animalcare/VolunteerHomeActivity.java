package com.Pie4u.animalcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

public class VolunteerHomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private String volunteerOrganization;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("com.Pie4u.animalcare", Context.MODE_PRIVATE);
        volunteerOrganization = sharedPreferences.getString("topic","topic");
        if(!volunteerOrganization.equals("topic"))
            subscribeToTopic(volunteerOrganization);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        viewPager = findViewById(R.id.viewPagerVolunteerHome);
        viewPager.setAdapter(new BottomNavPagerAdapter(getSupportFragmentManager(),getLifecycle()));
        viewPager.setUserInputEnabled(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.navigation_rescue:
                        viewPager.setCurrentItem(0,false);
                        return true;
                    case R.id.navigation_feed:
                        viewPager.setCurrentItem(1,false);
                        return true;
                    case R.id.navigation_profile:
                        viewPager.setCurrentItem(2,false);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //getUserOrg();

    }

    public void getUserOrg(){

        if(firebaseUser!=null){
            firebaseFirestore.collection("VolunteerProfileData")
                    .document(firebaseUser.getUid())
                    .addSnapshotListener(VolunteerHomeActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                            if(error!=null){
                                Log.d("SUBVOLUNTEER","failed");
                            }
                            else{
                                if(value!=null && value.exists()){
                                    String volunteerOrganization =value.getString("volunteerOrganization");
                                    if(volunteerOrganization!=null) {
                                        String topic = "topic";
                                        if (volunteerOrganization.equals("PFA Durg/Bhilai"))
                                            topic = "Durg/Bhilai";
//                                        else if(volunteerOrganization.equals("PFA Bhilai"))
//                                            topic = "Bhilai";
                                        else if (volunteerOrganization.equals("Pune NGO"))
                                            topic = "Pune";

                                        //subscribeToTopic(topic);
                                    }

                                }
                            }

                        }
                    });
        }
    }

    public void subscribeToTopic(final String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to Topic "+topic;
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe "+topic;
                        }
                        Log.i("SUBTOPIC",msg+ " "+ topic);
                        Toast.makeText(VolunteerHomeActivity.this, "Welcome back :)", Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    String msg = "Subscribed to Topic";
                    if (!task.isSuccessful()) {
                        msg = "Failed to subscribe";
                    }
                    Log.d("SUBUID","TRUE");
                    Log.i("SUBTOPICUID", msg);
                }
            });
        }
    }

}