package com.Pie4u.animalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AnimalRescueLoginActivity extends AppCompatActivity {

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(AnimalRescueLoginActivity.this,MainActivity.class);
//        finish();
//        startActivity(intent);
//    }
    private TextView textViewRescueSignUp;
    EditText volunteerPhoneNo , passwordET;
    Button loginbtn;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_rescue_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(AnimalRescueLoginActivity.this);
        progressDialog.setMessage("Checking your phone no...");
        progressDialog.setTitle("Account LogIn");
        progressDialog.setCancelable(false);
        volunteerPhoneNo = findViewById(R.id.EditTextRescueUsername);
        //passwordET = findViewById(R.id.EditTextRescuePassword);
        loginbtn = findViewById(R.id.BtnRescueLogin);
        textViewRescueSignUp = findViewById(R.id.textViewRescueSignUp);

        textViewRescueSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AnimalRescueLoginActivity.this,RescueSignUpActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = volunteerPhoneNo.getText().toString();
                //String password = passwordET.getText().toString();

                if(TextUtils.isEmpty(phoneNo)){
                    volunteerPhoneNo.setError("Please enter your Phone No.");
                    return;
                }
//                if(TextUtils.isEmpty(password)){
//                    passwordET.setError("Please enter the password!");
//                    return;
//                }

                if(!checkPhoneNo(phoneNo)){
                    Snackbar snackbar = Snackbar.make(volunteerPhoneNo, "Please enter 10 digit Phone No.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    snackbar.show();
                    return;
                }

                if(!TextUtils.isEmpty(phoneNo)){//&& !TextUtils.isEmpty(password)){

                    progressDialog.show();
                    checkVolunteerPhoneNo(phoneNo);
                    //startPhoneVerification(phoneNo);
//                    firebaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(AnimalRescueLoginActivity.this, "Login Successful :)", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(AnimalRescueLoginActivity.this,VolunteerHomeActivity.class);
//                                finish();
//                                startActivity(intent);
//                            }else{
//                                Toast.makeText(AnimalRescueLoginActivity.this, "Login Failed :)", Toast.LENGTH_SHORT).show();
//                                //Intent intent = new Intent(AnimalRescueLoginActivity.this,MainActivity.class);
//                                //finish();
//                                //startActivity(intent);
//                            }
 //                       }
 //                   });


                }

            }
        });

    }

    public void checkVolunteerPhoneNo(final String phoneNo){
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkOnDatabase(phoneNo);

                            //firebaseAuth.signOut();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SigInANO", "signInAnonymously:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            if(progressDialog.isShowing())
                                progressDialog.cancel();
                                // If sign in fails, display a message to the user.
                            Log.w("SigInANO", "signInAnonymously:failure", task.getException());
                            Toast.makeText(AnimalRescueLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                    }
                });
    }

    public void checkOnDatabase(final String phoneNo){
        Log.d("USERANO","TRUE");
        firebaseFirestore.collection("VolunteerProfileData")
                .whereEqualTo("volunteerPhoneNo",phoneNo)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                //firebaseAuth.signOut();
                if(progressDialog.isShowing())
                    progressDialog.cancel();

                if(firebaseAuth.getCurrentUser()!=null) {
                    firebaseAuth.getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DELETEACC", "User account deleted.");
                                        if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){

                                            startPhoneVerification(phoneNo);
                                            //Toast.makeText(AnimalRescueLoginActivity.this, "OK good", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Snackbar snackbar = Snackbar.make(volunteerPhoneNo, "Phone No. not registered, Please create a new account !", Snackbar.LENGTH_LONG);
                                            snackbar.setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            });
                                            snackbar.show();
                                        }
                                    }
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //firebaseAuth.signOut();
                if(progressDialog.isShowing())
                    progressDialog.cancel();

                if(firebaseAuth.getCurrentUser()!=null) {
                    firebaseAuth.getCurrentUser().delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("DELETEUUUSER", "User account deleted.");
                                    }
                                }
                            });
                    Log.d("USERANOERROR", e.toString());
                    Snackbar snackbar = Snackbar.make(volunteerPhoneNo, "Some error occurred, Please try ", Snackbar.LENGTH_LONG);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    snackbar.show();
                }
            }
        });
    }

    public void startPhoneVerification(String volunteerPhone){
        Intent intent = new Intent(AnimalRescueLoginActivity.this,PhoneVerificationActivity.class);
        intent.putExtra("phoneNo",volunteerPhone);
        intent.putExtra("isSignUp",false);
        startActivity(intent);
    }

    public boolean checkPhoneNo(String phoneNo){

        return phoneNo.matches("[0-9]{10}");
    }
}
