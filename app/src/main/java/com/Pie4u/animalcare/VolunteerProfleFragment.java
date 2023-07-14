package com.Pie4u.animalcare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VolunteerProfleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolunteerProfleFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VolunteerProfleFragment() {
        // Required empty public constructor
    }

    public static VolunteerProfleFragment newInstance(String param1, String param2) {
        VolunteerProfleFragment fragment = new VolunteerProfleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Button btnVolunteerLogout;
    private Button btnEditVolunteerProfile;
    private TextView textViewVolunteerName, textViewVolunteerPhoneNo,textViewVolunteerOrganization, textViewVolunteerEmail;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private View myView;
    private Uri imageUri;
    private EditText editTextEditName, editTextEditEmail;
    private ImageView imageViewVolunteerPhoto;
    private ImageView imageViewBottomSheetVolunteerPhoto;
    private Drawable drawableVolunteerPhoto;
    private Bitmap bitmapProfile;
    private byte[] byteArray;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImg");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_volunteer_profle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myView = view;
        progressDialog = new ProgressDialog(myView.getContext());
        btnVolunteerLogout = view.findViewById(R.id.btnVolunteerLogout);
        btnEditVolunteerProfile = view.findViewById(R.id.btnEditVolunteerProfile);
        imageViewVolunteerPhoto = view.findViewById(R.id.imageViewVolunteerPhoto);
        textViewVolunteerEmail = view.findViewById(R.id.textViewVolunteerEmail);
        textViewVolunteerPhoneNo = view.findViewById(R.id.textViewVolunteerPhoneNo);
        textViewVolunteerName = view.findViewById(R.id.textViewVolunteerName);
        textViewVolunteerOrganization = view.findViewById(R.id.textViewVolunteerOrganization);
        updateProfileUi();
        btnVolunteerLogout.setOnClickListener(this);
        imageViewVolunteerPhoto.setOnClickListener(this);
        btnEditVolunteerProfile.setOnClickListener(this);
        drawableVolunteerPhoto = imageViewVolunteerPhoto.getDrawable();

    }

    public void updateProfileUi(){

        if(firebaseUser!=null){
            firebaseFirestore.collection("VolunteerProfileData")
                    .document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                            if(error!=null){
                                Snackbar snackbar = Snackbar.make(myView, "Some error occurred", Snackbar.LENGTH_LONG);
                                snackbar.setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                snackbar.show();
                            }
                            else{
                                if(value!=null && value.exists()){

                                    textViewVolunteerName.setText(value.getString("volunteerName"));
                                    textViewVolunteerPhoneNo.setText(value.getString("volunteerPhoneNo"));
                                    textViewVolunteerOrganization.setText(value.getString("volunteerOrganization"));
                                    String imageUrl = value.getString("volunteerPhotoUrl");
                                    if(imageUrl!=null){
                                        Log.d("IMGURLIS",imageUrl);
                                        try {

                                            Glide.with(myView.getContext())
                                                    .load(imageUrl)
                                                    .error(R.drawable.dogprofile)
                                                    .placeholder(R.drawable.dogprofile)
                                                    .into(imageViewVolunteerPhoto);
                                        }catch (Exception e){
                                            Log.d("IMGEXC",e.toString());
                                        }
                                    }
                                    String email =value.getString("volunteerEmail");
                                    if(email!=null){
                                        if(!email.equals("")) {
                                            textViewVolunteerEmail.setText(email);
                                        }else{
                                            textViewVolunteerEmail.setText("Email");
                                        }
                                    }

                                }else{
                                    Snackbar snackbar = Snackbar.make(myView, "Please fill your profile", Snackbar.LENGTH_LONG);
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

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnVolunteerLogout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "LogOut Successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MainActivity.class);
           // requireActivity().finish();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if(view.getId() == R.id.btnEditVolunteerProfile){
            openEditProfileBottomSheet();
        }
    }

    public void openEditProfileBottomSheet(){
        bitmapProfile = null;
        drawableVolunteerPhoto = imageViewVolunteerPhoto.getDrawable();
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(myView.getContext()//Objects.requireNonNull(VolunteerProfleFragment.this.requireContext())
                ,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(myView.getContext())//VolunteerProfleFragment.this.requireContext())
                .inflate(R.layout.bottom_sheet_volunteer_profile_edit,
                        (LinearLayout) myView.findViewById(R.id.linearLayoutBottomSheetProfileContainer));
        editTextEditName = bottomSheetView.findViewById(R.id.editTextEditVolunteerName);
        editTextEditName.setText(textViewVolunteerName.getText().toString());
        editTextEditEmail = bottomSheetView.findViewById(R.id.editTextEditVolunteerEmail);
        editTextEditEmail.setText(textViewVolunteerEmail.getText().toString());
        imageViewBottomSheetVolunteerPhoto = bottomSheetView.findViewById(R.id.imageViewEditVolunteerImage);
        imageViewBottomSheetVolunteerPhoto.setImageDrawable(drawableVolunteerPhoto);
        bottomSheetView.findViewById(R.id.btnUpdateVolunteerProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating your profile, just a moment....");
                progressDialog.setTitle("Profile Update");
                progressDialog.setCancelable(false);
                progressDialog.show();
                uploadImage(bottomSheetDialog);
            }
        });

        imageViewBottomSheetVolunteerPhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                choosePhotoDialog();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void updateVolunteerProfile(final BottomSheetDialog bottomSheetDialog,String profileImgUrl){

        final String volunteerName = editTextEditName.getText().toString();
        final String volunteerEmail = editTextEditEmail.getText().toString();
        if(!volunteerName.isEmpty()){
            Map<String,Object> updatesMap = new HashMap<>();
            updatesMap.put("volunteerName",volunteerName);
            if(profileImgUrl!=null)
                updatesMap.put("volunteerPhotoUrl",profileImgUrl);
            updatesMap.put("volunteerEmail",volunteerEmail);
            firebaseFirestore.collection("VolunteerProfileData")
                    .document(firebaseUser.getUid())
                    .update(updatesMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    if(bottomSheetDialog.isShowing())
                        bottomSheetDialog.cancel();
                    textViewVolunteerName.setText(volunteerName);
                    textViewVolunteerEmail.setText(volunteerEmail);
                    Toast.makeText(myView.getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(progressDialog.isShowing())
                        progressDialog.cancel();
                    if(bottomSheetDialog.isShowing())
                        bottomSheetDialog.cancel();
                    Toast.makeText(myView.getContext(), "Some error occurred, Please try again !", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            if(progressDialog.isShowing())
                progressDialog.cancel();
            Toast.makeText(myView.getContext(), "Please enter your name !", Toast.LENGTH_SHORT).show();
        }
    }

    public void choosePhotoDialog(){

        try {
            View photoOptionView = getLayoutInflater().inflate(R.layout.bottom_sheet_photo_option, null);
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(myView.getContext());
            alertDialogBuilder
                    .setView(photoOptionView)
                    .setCancelable(true);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            photoOptionView.findViewById(R.id.imageViewCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(myView.getContext(), "Camera", Toast.LENGTH_SHORT).show();

                    if(ContextCompat.checkSelfPermission(myView.getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                    }
                    else{
                        if(alertDialog.isShowing())
                            alertDialog.cancel();
                        getImageFromCamera();
                    }

                }
            });
            photoOptionView.findViewById(R.id.imageViewGallery).setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    //Toast.makeText(myView.getContext(), "Gallery", Toast.LENGTH_SHORT).show();
                    if(requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else
                    {
                        if(alertDialog.isShowing())
                            alertDialog.cancel();
                        getPhoto();
                    }

                }
            });
            alertDialog.show();
        }
        catch (Exception e){
            if(progressDialog.isShowing())
                progressDialog.cancel();
            Log.d("ERROREXCEPTION",e.toString());
        }

    }


    private void uploadImage(final BottomSheetDialog bottomSheetDialog)
    {
        if(bitmapProfile!=null && firebaseUser!=null) {
            byteArray = getImageBitmapByteArray(bitmapProfile);
            if (byteArray != null && byteArray.length > 0){
                final StorageReference ref = storageReference.child(firebaseUser.getUid());
                    ref.putBytes(byteArray)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    updateVolunteerProfile(bottomSheetDialog,downloadUrl);
                                    Toast.makeText(myView.getContext(), "Photo uploaded", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(myView.getContext(), "Erro uploading photo", Toast.LENGTH_LONG).show();
                                }
                            });

                            Log.i("yessss", "onSuccess");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(myView.getContext(), "Error while Uploading Photo,Please Try Again", Toast.LENGTH_LONG).show();
                            if(progressDialog.isShowing())
                                progressDialog.cancel();
                            Log.i("NOOOOO", "onFailure: ");
                        }
                    });
        }

        }
        else {
            updateVolunteerProfile(bottomSheetDialog,null);
            //Toast.makeText(myView.getContext(), "Please choose a photo", Toast.LENGTH_LONG).show();
        }
    }

    public void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }

        if(requestCode == 2 &&  grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED ){
            getImageFromCamera();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data !=null)
        {
            Uri selectedImage=data.getData();

            try{
                bitmapProfile = MediaStore.Images.Media.getBitmap(myView.getContext().getContentResolver(),selectedImage);
                imageViewBottomSheetVolunteerPhoto.setImageBitmap(bitmapProfile);
                //byteArray = getImageBitmapByteArray(bitmapProfile);
                Log.d("IMGBYTEARRG","TRUE");
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        if(requestCode == 2 && resultCode == RESULT_OK ) {

            try {
                bitmapProfile = MediaStore.Images.Media.getBitmap(
                        myView.getContext().getContentResolver(), imageUri);
                imageViewBottomSheetVolunteerPhoto.setImageBitmap(bitmapProfile);
                //byteArray = getImageBitmapByteArray(bitmapProfile);
                Log.d("IMGBYTEARRCAME","TRUE");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void getImageFromCamera(){

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = myView.getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        assert imageUri != null;
        Log.i("IMGURI2","IMAGE URL "+ imageUri.toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 2);
        Log.i("IMGURI2","IMAGE URL"+ imageUri.toString());
    }

    public byte[] getImageBitmapByteArray(Bitmap bitmapProfileImage){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}