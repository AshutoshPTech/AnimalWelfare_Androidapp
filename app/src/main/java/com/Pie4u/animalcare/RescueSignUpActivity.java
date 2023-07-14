package com.Pie4u.animalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RescueSignUpActivity extends AppCompatActivity {

    private Spinner spinnerOrganization;
    String volunteerName;
    String volunteerPhone ;
    String volunteerEmail ;
    ArrayList<String> spinnerArrayList;
    private  ArrayList<String> organisationList=new ArrayList<>();
    private  ArrayList<String> cityList= new ArrayList<>();

    private Button btnVolunteerSignUp;
    private EditText editTextVolunteerName, editTextVolunteerPhone, editTextVolunteerEmail, editTextVolunteerOrgCode;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_sign_up);

        spinnerOrganization = findViewById(R.id.spinnerOrganization);
        btnVolunteerSignUp = findViewById(R.id.btnVolunteerSignUp);
        editTextVolunteerName = findViewById(R.id.editTextVolunteerName);
        editTextVolunteerPhone = findViewById(R.id.editTextVolunteerPhone);
        editTextVolunteerEmail = findViewById(R.id.editTextVolunteerEmail);
        editTextVolunteerOrgCode = findViewById(R.id.editTextVolunteerOrgCode);

//        setUpOrgNames();
        //setUpSpinnerOrganization();
        getOrganisationList();


        btnVolunteerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkVolunteerOrgCode();
                // retrieveUniqueCodes();
            }
        });

    }

    public void checkVolunteerOrgCode(){
        if (organisationList.size()>0){

            volunteerName = editTextVolunteerName.getText().toString().trim();
            volunteerPhone = editTextVolunteerPhone.getText().toString().trim();
            volunteerEmail = editTextVolunteerEmail.getText().toString().trim();
            final String volunteerOrgCode = editTextVolunteerOrgCode.getText().toString().trim();
            int volunteerOrgPos = spinnerOrganization.getSelectedItemPosition();

            if(TextUtils.isEmpty(volunteerName)){
                editTextVolunteerName.setError("Please enter your name !");
                editTextVolunteerName.requestFocus();
                return;
            }
            if(TextUtils.isEmpty(volunteerPhone)){
                editTextVolunteerPhone.setError("Please enter your Phone No. !");
                editTextVolunteerPhone.requestFocus();
                return;
            }
            if(TextUtils.isEmpty(volunteerOrgCode) || volunteerOrgCode.length()<6){
                editTextVolunteerOrgCode.setError("Please enter correct Organization Unique Code !");
                editTextVolunteerOrgCode.requestFocus();
                return;
            }
            if(volunteerOrgPos==0){
                Snackbar snackbar = Snackbar.make(spinnerOrganization,"Please select your organization",Snackbar.LENGTH_LONG);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackbar.show();
                return;
            }

            if(!checkPhoneNo(volunteerPhone)){
                Snackbar snackbar = Snackbar.make(spinnerOrganization,"Please enter 10 digit Phone Number",Snackbar.LENGTH_LONG);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackbar.show();
                return;
            }
            retrieveUniqueCodes(volunteerOrgPos,volunteerOrgCode);

//
//        else{ if(orgUniqueCodes.get(volunteerOrgPos).equals(volunteerOrgCode)){
//////            startPhoneVerification(volunteerOrgPos);
//////        }
//            String msg = "Organization code not correct :(";
//            Snackbar snackbar = Snackbar.make(spinnerOrganization,msg,Snackbar.LENGTH_LONG);
//            snackbar.setAction("OK", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//            snackbar.show();
        }
    }

    public void startPhoneVerification(int volunteerOrgPos){
        Intent intent = new Intent(RescueSignUpActivity.this,PhoneVerificationActivity.class);
        intent.putExtra("phoneNo",volunteerPhone);
        intent.putExtra("email",volunteerEmail);
        intent.putExtra("name",volunteerName);
        intent.putExtra("isSignUp",true);
        intent.putExtra("volunteerOrganization",organisationList.get(volunteerOrgPos));
        intent.putExtra("city",cityList.get(volunteerOrgPos-1));
        startActivity(intent);
    }

    public void retrieveUniqueCodes(final int volunteerOrgPos, final String volunteerOrgCode){

        firebaseFirestore.collection("RescueOrganization")
                .document(organisationList.get(volunteerOrgPos))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.e("TAG", "onSuccess:111 "+ documentSnapshot.toString() );
                        if(documentSnapshot!=null && documentSnapshot.exists()) {
                            Log.e("TAG", "onSuccess:222 " );
                            String orgUniqueCode = documentSnapshot.getString("uniqueCode");
                            assert orgUniqueCode != null;
                            String msg = "";
                            if(orgUniqueCode.equals(volunteerOrgCode)){
                                msg = "Success";
                                startPhoneVerification(volunteerOrgPos);
                            }else{
                                msg = "Organization code not correct :(";
                            }
                            Snackbar snackbar = Snackbar.make(spinnerOrganization,msg,Snackbar.LENGTH_LONG);
                            snackbar.setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                        Log.e("TAG", "onSuccess:444 " );
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR404", Objects.requireNonNull(e.toString()));
                        Snackbar snackbar = Snackbar.make(spinnerOrganization,"Some error occurred, try again :)",Snackbar.LENGTH_LONG);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        snackbar.show();
                    }
                });
    }

    public boolean checkPhoneNo(String phoneNo){

        return phoneNo.matches("[0-9]{10}");
    }

    public void setUpSpinnerOrganization(){

        spinnerArrayList = new ArrayList<>();
        spinnerArrayList.add("Select your organization");
        spinnerArrayList.add("PFA Durg/Bhilai");
        //spinnerArrayList.add("PFA Bhilai");
        spinnerArrayList.add("Pune NGO");

    }

    /* public void setUpOrgNames(){
         orgNames.add("Select your organization");
         orgNames.add("PFA_DURG/BHILAI");
         //orgNames.add("PFA_BHILAI");
         orgNames.add("PUNE_NGO");

         orgUniqueCodes.add("");
         orgUniqueCodes.add("ay8p3m");
        // orgUniqueCodes.add("qn6z7j");
      //   orgUniqueCodes.add("vt71rb");
     }*/
    private void getOrganisationList(){

        FirebaseFirestore ff= FirebaseFirestore.getInstance();
        CollectionReference cr= ff.collection("RescueOrganization");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null || value==null){
                    Toast.makeText(RescueSignUpActivity.this,"Error null organization list",Toast.LENGTH_LONG).show();
                    return;
                }

                organisationList.clear();
                organisationList.add("Select Organisation");

                cityList.clear();
                for(DocumentSnapshot ds : value){
                    if(ds.exists())
                        organisationList.add(ds.get("name").toString());
                    cityList.add(ds.get("city").toString());
                }

                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<>(RescueSignUpActivity.this,android.R.layout.simple_spinner_dropdown_item,organisationList);
                spinnerOrganization.setAdapter(arrayAdapter);
            }
        });
    }
}