package com.Pie4u.animalcare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RescueRequestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String volunteerOrganization;

    private RecyclerView recyclerView;
    private RescueRequestRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<AnimalHelpCase> rescueCardArrayList;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;
    private FloatingActionButton citybtn;
    private Spinner spinner;

    private View myView;

    public RescueRequestsFragment() {
        // Required empty public constructor
    }


    public static RescueRequestsFragment newInstance(String param1, String param2) {
        RescueRequestsFragment fragment = new RescueRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting rescue details...");
        progressDialog.setTitle("Rescue...");
        progressDialog.setCancelable(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rescue_requests, container, false);
    }

    public void updateHelpRequests(final View view){


        firebaseFirestore.collection("Cases")
                .document("Topic")
                .collection(volunteerOrganization)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Snackbar.make(view,"Some error occurred :(",Snackbar.LENGTH_LONG)
                            .show();
                    Log.d("help error", "onEvent: "+error.toString());
                }else {
                    if(value==null || value.isEmpty()){
                        Snackbar snackbar = Snackbar.make(view,"No recent requests !",Snackbar.LENGTH_LONG);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                        snackbar.setActionTextColor(Color.GREEN);
                        snackbar.show();
                    }else{
                        rescueCardArrayList.clear();
                        for(DocumentSnapshot documentSnapshot : value){
                            if(documentSnapshot!=null) {
                                String className = Objects.requireNonNull(documentSnapshot.get("photourl")).getClass().getName();
                                Log.d("OBJCLASS", "" + className);
                                if (className.equals("java.util.ArrayList")){
                                    AnimalHelpCase animalHelpCase = documentSnapshot.toObject(AnimalHelpCase.class);
                                    if (animalHelpCase != null) {
                                        animalHelpCase.setRescueDocumentId(documentSnapshot.getId());
                                        if (documentSnapshot.getBoolean("isCompleted") != null)
                                            animalHelpCase.setCompleted(documentSnapshot.getBoolean("isCompleted"));
                                        Log.d("ISCOPO", " -> " + animalHelpCase.isCompleted());
                                        rescueCardArrayList.add(animalHelpCase);
                                    }
                            }
                            }
                        }
                        setUpRecyclerview();
                    }
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_rescue_requests);
        rescueCardArrayList = new ArrayList<>();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("com.Pie4u.animalcare", Context.MODE_PRIVATE);
        volunteerOrganization = sharedPreferences.getString("topic","topic");
        Log.d("topic info", "onViewCreated: "+volunteerOrganization);
        updateHelpRequests(view);
        myView = view;
        citybtn =view.findViewById(R.id.cityChangeBtn);

        citybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCity();
            }
        });


    }

    private void changeCity(){
        FirebaseFirestore ff= FirebaseFirestore.getInstance();
        CollectionReference cr= ff.collection("Cities");
        final ArrayList<String> cities= new ArrayList<>();

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(myView.getContext()//Objects.requireNonNull(VolunteerProfleFragment.this.requireContext())
                ,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext())//VolunteerProfleFragment.this.requireContext())
                .inflate(R.layout.bottom_change_city,
                        (LinearLayout) myView.findViewById(R.id.linear_bottom_city_change));
        //updatecitybtn = bottomSheetView.findViewById(R.id.cityChangeBtn);
        spinner = bottomSheetView.findViewById(R.id.spinner_change_city);

        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                cities.clear();
                cities.add("Select New City");

                for(DocumentSnapshot ds :value){
                    cities.add(ds.get("name").toString());
                }
                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<>(myView.getContext(),android.R.layout.simple_spinner_dropdown_item,cities);
                spinner.setAdapter(arrayAdapter);
            }
        });
        bottomSheetView.findViewById(R.id.btnUpdateVolunteerCity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItemPosition()!=0){
                unSubscribeToTopic(spinner.getSelectedItem().toString());
                Toast.makeText(myView.getContext(),"City changed",Toast.LENGTH_LONG).show();
                bottomSheetDialog.cancel();}
                else
                {
                    Toast.makeText(myView.getContext(),"Please select a city",Toast.LENGTH_LONG).show();
                }
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void setUpRecyclerview(){

        recyclerViewAdapter = new RescueRequestRecyclerViewAdapter(rescueCardArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RescueRequestRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, final View itemView) {

                switch (view.getId()){

                    case R.id.btnAcceptRescue:
                        //getRescueData(position);
                        progressDialog.show();
                        updateRescuer(position);
                        //Toast.makeText(getContext(), "ACCEPT rescue " +
                        //        rescueCardArrayList.get(position).getAnimalType(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.cardRescueRequests:
                        AnimalHelpCase helpCase = rescueCardArrayList.get(position);
                        if(!helpCase.isAccepted() ||
                                (helpCase.getRescuerUid()!=null && !helpCase.getRescuerUid().equals("null")
                                        && helpCase.getRescuerUid().equals(firebaseUser.getUid()) )){
                            getRescueData(position);
                        }
                        break;

                }

            }
        });

    }

    public void getRescueData(int position){
        if(position>=0) {
            AnimalHelpCase rescueCase = rescueCardArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("USERNAME", rescueCase.getUserName());
            String userNo = "user no";
            if(rescueCase.getUserNo()!=null)
                userNo = rescueCase.getUserNo();

            bundle.putString("PHONENO", ""+userNo);
            bundle.putString("ANIMALTYPE", rescueCase.getAnimalType());
            bundle.putString("LAT", String.valueOf(rescueCase.getLatitude()));
            bundle.putString("LNG", String.valueOf(rescueCase.getLongitude()));
            bundle.putString("URL",rescueCase.getPhotourl().toString());
           // for(int i=0;i<rescueCase.getPhotourl().size();i++)
            //{
            //    bundle.putString("URL"+i, rescueCase.getPhotourl().get(i));
           // }

            bundle.putString("Location", rescueCase.getUserLocation());
            bundle.putString("cityType", rescueCase.getCityType());
            bundle.putString("description",rescueCase.getDesc());
            bundle.putString("time",rescueCase.getTimestamp().toDate().toString());
            bundle.putBoolean("accepted",rescueCase.isAccepted());
            bundle.putString("rescuerUid",rescueCase.getRescuerUid());
            bundle.putString("rescueDocumentId",rescueCase.getRescueDocumentId());
            bundle.putBoolean("isCompleted",rescueCase.isCompleted());
            bundle.putString("FromWhere", "NOTIF");

            Intent intent = new Intent(getContext(),RescueActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void updateRescuer(final int position){
        AnimalHelpCase helpCase = rescueCardArrayList.get(position);
        if(!helpCase.isAccepted() && !helpCase.isCompleted()) {

            Map<String, Object> updateRescueData = new HashMap<>();
            updateRescueData.put("accepted", true);
            updateRescueData.put("rescueStatus", "Rescue in progress");
            updateRescueData.put("rescuerUid", firebaseUser.getUid());
            firebaseFirestore.collection("Cases")
                    .document("Topic")
                    .collection(rescueCardArrayList.get(position).getCityType())
                    .document(rescueCardArrayList.get(position).getRescueDocumentId())
                    .update(updateRescueData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    rescueCardArrayList.get(position).setAccepted(true);
                    rescueCardArrayList.get(position).setRescuerUid(firebaseUser.getUid());
                    rescueCardArrayList.get(position).setRescueStatus("Rescue in progress");
                    getRescueData(position);
                    if (progressDialog.isShowing())
                        progressDialog.cancel();
                    Toast.makeText(getContext(), "Rescue details updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (progressDialog.isShowing())
                        progressDialog.cancel();
                    Toast.makeText(getContext(), "Failed to update rescue details", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            if (progressDialog.isShowing())
                progressDialog.cancel();
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
                            return;
                        }
                        //updateHelpRequests(myView);
                        volunteerOrganization = topic;
                        SharedPreferences sharedPreferences = myView.getContext().getSharedPreferences("com.Pie4u.animalcare", Context.MODE_PRIVATE);
                        sharedPreferences.edit().putString("topic",topic).apply();
                        Log.i("SUBTOPIC",msg+ " "+ topic);
                        Toast.makeText(myView.getContext(), "Welcome to "+topic, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(myView.getContext(), VolunteerHomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });

        }

    public void unSubscribeToTopic(final String item){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(volunteerOrganization)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "unSubscribed to Topic "+volunteerOrganization;
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe "+volunteerOrganization;
                            return;
                        }
                        subscribeToTopic(item);
                    }
                });

    }





//    public void createAlertDialog(final int position, final View itemView){
//        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
//                .setTitle("Decline help request")
//                .setMessage("Do you want to decline help request ?")
//                .setCancelable(true)
//                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        hideRescueButtons(position, itemView);
//                        Toast.makeText(getContext(), "help request declined", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton("NO",null)
//                .show();
//    }
//
//    public void hideRescueButtons(int position, View itemView){
//        itemView.findViewById(R.id.linearLayoutRescueBtn).setVisibility(View.GONE);
//    }
}