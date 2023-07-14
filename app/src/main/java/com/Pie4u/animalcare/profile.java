package com.Pie4u.animalcare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

static TextView a,b,c;
CircleImageView image;
private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
private ProgressDialog progressDialog;

    FirebaseStorage storage=FirebaseStorage.getInstance();
    FirebaseFirestore firestore=FirebaseFirestore.getInstance();
    FirebaseUser u= FirebaseAuth.getInstance().getCurrentUser();
    StorageReference sr=storage.getReference("ProfileImg").child(u.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        a= findViewById(R.id.namet);
        b= findViewById(R.id.aboutt);
        c= findViewById(R.id.contactt);

        progressDialog = new ProgressDialog(profile.this);
        progressDialog.setMessage("Loading your Profile !");

        progressDialog.setCancelable(false);
        progressDialog.show();

        image=findViewById(R.id.profileI);

        Button edit=findViewById(R.id.Editbtn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(profile.this, editprofile.class);
                intent.putExtra("image",getImageBitmapByteArray());
                intent.putExtra("name",a.getText().toString());
                intent.putExtra("about",b.getText().toString());
                intent.putExtra("contact",c.getText().toString());

                startActivity(intent);
            }
        });

        updateProfile();

    }

    public byte[] getImageBitmapByteArray(){
        Bitmap bitmapProfileImage = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapProfileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void updateProfile(){

        firestore.collection("ProfileData")
                .document(firebaseUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null)
                        {
                            Toast.makeText(profile.this,"Error",Toast.LENGTH_LONG).show();
                            progressDialog.cancel();
                        }
                        else if(value!=null && value.get("name")!=null){

                            String name = Objects.requireNonNull(value.get("name")).toString();
                            String about = Objects.requireNonNull(value.get("about")).toString();
                            String no = Objects.requireNonNull(value.get("no")).toString();
                            String imageUrl = Objects.requireNonNull(value.get("imageUrl")).toString();

                            editprofile x;

                            a.setText(name);
                            b.setText(about);
                            c.setText(no);

                            Glide.with(getApplicationContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.dogprofile)
                                    .error(R.drawable.dogprofile)
                                    .into(image);
                            if(progressDialog.isShowing())
                                progressDialog.cancel();
                        }
                    }
                });
    }

}
