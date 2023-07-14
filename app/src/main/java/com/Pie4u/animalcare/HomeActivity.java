package com.Pie4u.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ViewPager viewPager;
    static FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAZki8rck:APA91bGIdYfinRDbRf51zGXOfIdZlFFZsswRgjaCn3DqJF2WSwXlRo_oW-EHtO7MQ-jjJDeFlhzB_6nLx2Gayy6ht7p0M0oiGCc9N1fnKa-sRPbpCuuNCfFKUAE4NlDegoYpabMexzSS";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";
    private Toolbar tb;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(HomeActivity.this);
        menuInflater.inflate(R.menu.menu_items,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.ItemLogout){

            firebaseAuth.signOut();
            Toast.makeText(this, "LogOut Successfull", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(HomeActivity.this, MainActivity.class);
            finish();
            startActivity(i);
            return true;
        }
        else if(item.getItemId() == R.id.ItemProfile){

            Intent j = new Intent(HomeActivity.this, profile.class);
            startActivity(j);
            return true;
        }
        else if(item.getItemId() == R.id.adp){

            Intent j = new Intent(HomeActivity.this, bs_ad.class);
            startActivity(j);
            return true;
        }

        return false;

    }

    public void sendNotify(){

        TOPIC ="/topics/animalhelp";// "/topics/userABC"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "ANIMAL CASES";//edtTitle.getText().toString();
        NOTIFICATION_MESSAGE = "please help";//edtMessage.getText().toString();

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);

            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            Log.e(TAG, "onCreate: " + e.getMessage() );
        }
        sendNotification(notification);

    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
//                        edtTitle.setText("");
//                        edtMessage.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        subscribeToTopic();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://animalcare-b79a9-default-rtdb.firebaseio.com/");
        tb=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        viewPager = findViewById(R.id.viewPager);
        Log.d("HEREREFRAG","true");
        viewPager.setAdapter(new AnimalFragmentAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if(firebaseAuth.getCurrentUser()!=null) {
            firebaseFirestore.collection("ProfileData")
                    .document(firebaseAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (!documentSnapshot.exists()) {
                                Intent i = new Intent(HomeActivity.this, editprofile.class);
                                i.putExtra("home", 1);
                                finish();
                                startActivity(i);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, "Some error occurred.", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    public void subscribeToTopic(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null) {
            FirebaseMessaging.getInstance().subscribeToTopic(firebaseUser.getUid())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String msg = "Subscribed to Topic";//getString(R.string.msg_subscribed);
                            if (!task.isSuccessful()) {
                                msg = "Failed to subscribe";//getString(R.string.msg_subscribe_failed);
                            }
                            Log.d("SUBUIDH","TRUE");
                            Log.i("SUBTOPICUIDH", msg);
                            Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
