package com.Pie4u.animalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private EditText editTextVerificationCode;

    private String mVerificationId, phoneNo, email,name, volunteerOrganization,city;
    private ProgressBar progressBarPhoneVerification;
    private ProgressDialog progressDialog;
    private Button btnVerifyPhoneNo;
    private Boolean isSignUp;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        progressDialog = new ProgressDialog(PhoneVerificationActivity.this);

        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        btnVerifyPhoneNo = findViewById(R.id.btnVerifyPhoneNo);
        progressBarPhoneVerification = findViewById(R.id.progressBarPhoneVerification);
        progressBarPhoneVerification.setVisibility(View.GONE);
        phoneNo = getIntent().getStringExtra("phoneNo");
        isSignUp = getIntent().getBooleanExtra("isSignUp",false);
        if(getIntent().getBooleanExtra("isSignUp",false)){
            email = getIntent().getStringExtra("email");
            city = getIntent().getStringExtra("city");
            name = getIntent().getStringExtra("name");
            volunteerOrganization = getIntent().getStringExtra("volunteerOrganization");
        }
        //Toast.makeText(this, phoneNo, Toast.LENGTH_SHORT).show();

        sendVerificationCode(phoneNo);

        btnVerifyPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verificationCode = editTextVerificationCode.getText().toString().trim();
                if (verificationCode.isEmpty() || verificationCode.length() < 6) {
                    editTextVerificationCode.setError("Enter valid code");
                    editTextVerificationCode.requestFocus();
                    return;
                }

                verifyVerificationCode(verificationCode);
            }
        });
    }


    private void sendVerificationCode(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            String verificationCode = phoneAuthCredential.getSmsCode();

            if (verificationCode != null) {
                editTextVerificationCode.setText(verificationCode);
                verifyVerificationCode(verificationCode);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        progressBarPhoneVerification.setVisibility(View.VISIBLE);
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Log.d("SOMEERROR",e.toString());
            Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBarPhoneVerification.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d("taskPHONEAUTH","SUCCESSFULL");

                            if(!isSignUp){
                                getUserOrg();
                                return;
                            }
                            if(task.getResult()!=null) {
                                FirebaseUser firebaseUser = task.getResult().getUser();
                                saveProfileData(firebaseUser);
                            }
                        } else {

                            Log.d("taskPHONEAUTH","Failed");
                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    public void startVolunteerHome(){

        Intent intent = new Intent(PhoneVerificationActivity.this, VolunteerHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void saveProfileData(FirebaseUser firebaseUser) {

        if (firebaseUser != null) {
            progressDialog.setTitle("Account Creation");
            progressDialog.setMessage("Creating your account...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Map<String,Object> profileDataMap = new HashMap<>();
            profileDataMap.put("volunteerName",name);
            profileDataMap.put("volunteerEmail",email);
            profileDataMap.put("volunteerCity",city);
            profileDataMap.put("volunteerPhoneNo",phoneNo);
            profileDataMap.put("volunteerOrganization",volunteerOrganization);

            firebaseFirestore.collection("VolunteerProfileData")
                    .document(firebaseUser.getUid())
                    .set(profileDataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(progressDialog.isShowing())
                        progressDialog.cancel();

                    String topic = city;

                    SharedPreferences sharedPreferences = getSharedPreferences("com.Pie4u.animalcare", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("topic",topic).apply();
                    Toast.makeText(PhoneVerificationActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    startVolunteerHome();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), "Some error occurred", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    snackbar.show();
                }
            });
        }
    }

    public void getUserOrg(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            progressDialog.setMessage("Logging In..., just a moment");
            progressDialog.setTitle("Account Login");
            progressDialog.setCancelable(false);
            progressDialog.show();
            firebaseFirestore.collection("VolunteerProfileData")
                    .document(firebaseUser.getUid())
                    .addSnapshotListener(PhoneVerificationActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                            if(progressDialog.isShowing())
                                progressDialog.cancel();

                            if(error!=null){
                                Log.d("SUBVOLUNTEER","failed");
                            }
                            else{
                                if(value!=null && value.exists()){
                                    String volunteerOrganization =value.getString("volunteerOrganization");
                                    if(volunteerOrganization!=null) {
                                        String topic=value.getString("volunteerCity");

                                        SharedPreferences sharedPreferences = getSharedPreferences("com.sjcoders.mynotesapp", Context.MODE_PRIVATE);
                                        sharedPreferences.edit().putString("topic",topic).apply();
                                        startVolunteerHome();
                                    }

                                }
                            }

                        }
                    });
        }
    }


}