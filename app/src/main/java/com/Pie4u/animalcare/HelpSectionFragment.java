package com.Pie4u.animalcare;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaDrm;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.tasks.Tasks.whenAllSuccess;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpSectionFragment extends Fragment {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private TextView userLocationTv;
    private EditText desc;
    private List<Address> addressList;
    private Button getHelpBtn,selfhelpbtn,uploadImage;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase  firebaseDatabase;
    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private Location location;
    private ImageView animalImageView,test;
    private Bitmap animalBitmap;
    private FirebaseStorage firebaseStorage;
    //private StorageReference storageReference;
    private DatabaseReference myDatabaseRef;
    private String uniqueId;
    private Spinner spinner;
    private Spinner spinner2;
    private String animalType, Pname="name",Pno="0",url,cityType;
    private Uri imageUri;
    private int indexDot=0;

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    //private ImageView[] dots;
    ArrayList<String> animalList= new ArrayList<>();
    ArrayList<String> cityList= new ArrayList<>();

    ArrayList<ImageView> dots=new ArrayList<ImageView>();
    //ArrayList<Integer> images=new ArrayList<Integer>();
    ArrayList<Bitmap> imagebitmap=new ArrayList<Bitmap>();
    ArrayList<String> imageurl=new ArrayList<String>();
    ArrayList<UploadTask> uploadtasks=new ArrayList<UploadTask>();
    ArrayList<Task<Uri>> taskArrayList = new ArrayList<>();
    ArrayList<StorageReference> storageReferenceArrayList = new ArrayList<>();

    MyCustomPagerAdapter myCustomPagerAdapter;


    private ProgressDialog progressDialog;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAZki8rck:APA91bGIdYfinRDbRf51zGXOfIdZlFFZsswRgjaCn3DqJF2WSwXlRo_oW-EHtO7MQ-jjJDeFlhzB_6nLx2Gayy6ht7p0M0oiGCc9N1fnKa-sRPbpCuuNCfFKUAE4NlDegoYpabMexzSS";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

   private String NOTIFICATION_TITLE;
   private String NOTIFICATION_MESSAGE;
   private String TOPIC;
    //AnimalHelpCase helpCase;


    public HelpSectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help_section, container, false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("HEREIS","FALSE");
            getLiveLocation();
        }

        if(requestCode == 2 &&  grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED ){
            getImageFromCamera();
        }

    }

    public void getLiveLocation() {



        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i("HEREIS","TRUE");
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }



        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location==null)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Geocoder geocoder =new Geocoder(getContext());
        try {
            if(location != null) {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addressList.get(0) != null) {
                    Address address = addressList.get(0);
                    //Log.i("USERLOCATION", address.getThoroughfare() + " <->" + address.getSubThoroughfare());
                    userLocationTv.setText(address.getAddressLine(0));
                    Log.i("USERLOC", addressList.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void sendNotify(AnimalHelpCase helpCase){

        TOPIC ="/topics/"+helpCase.getCityType();// "/topics/userABC"; //topic must match with what the receiver subscribed to
        NOTIFICATION_TITLE = "ANIMAL HELP REQUIRED";//edtTitle.getText().toString();
        NOTIFICATION_MESSAGE = "This animal is injured , Please Help !";//edtMessage.getText().toString();

        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            //if(helpCase.getUserName()!=null){
            Log.i("HELPSECTIONACTUSER",helpCase.getUserName());
            Log.i("HELPSECTIONACTANI",helpCase.getAnimalType());
            notifcationBody.put("userName",Pname);
            notifcationBody.put("mobileNo",Pno);
            notifcationBody.put("url",helpCase.getPhotourl());
            notifcationBody.put("animalType",helpCase.getAnimalType());
            notifcationBody.put("location",helpCase.getUserLocation());
            notifcationBody.put("lat",String.valueOf(helpCase.getLatitude()));
            notifcationBody.put("lng",String.valueOf(helpCase.getLongitude()));
            notifcationBody.put("description",helpCase.getDesc());
            notifcationBody.put("cityType",helpCase.getCityType());
            notifcationBody.put("rescueDocumentId",helpCase.getRescueDocumentId());
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("h:mm a dd-MMM-yyyy ");
            String formattedDate = df.format(c);
            notifcationBody.put("time", formattedDate);
            //}

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
                        Toast.makeText(getContext(), "Request sent Successfully", Toast.LENGTH_LONG).show();
                        imagebitmap.clear();

                        indexDot=0;
                       sliderDotspanel.removeAllViews();

                        removedots();

                        Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();
                        progressDialog.cancel();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error", Toast.LENGTH_LONG).show();
                        progressDialog.cancel();
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
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void getImageFromCamera(){
        //get read write permission
        if(checkPermissionREAD_EXTERNAL_STORAGE(getContext())){
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                // Log.i("IMGURI2","IMAGE URL "+ imageUri.toString());
                //imageuri.add(imageUri.toString());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
                //Log.i("IMGURI2","IMAGE URL"+ imageUri.toString());
            }
// Else ask for permission
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]
                        { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
            }


            }

    }
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    public void showDialogforlocation(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                1);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onactivityHEREIS","TRUE");
        Log.d("IMGURI","-> "+ imageUri.toString());

        if(requestCode == 1 && resultCode == RESULT_OK ) {

            try {
                dotscount+=1;
                displaydot();
                animalBitmap = MediaStore.Images.Media.getBitmap(
                        getContext().getContentResolver(), imageUri);
                Log.d("BITMAP", animalBitmap.toString());
                imagebitmap.add(animalBitmap);
                viewPager.getAdapter().notifyDataSetChanged();
                //animalImageView.setImageBitmap(animalBitmap);
                Log.i("IMGURI","-> "+ imageUri.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        desc=view.findViewById(R.id.desc);
        userLocationTv = view.findViewById(R.id.TvLocation);
        animalImageView = view.findViewById(R.id.imageViewAnimal);
        getHelpBtn = view.findViewById(R.id.BtnGetHelp);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://animalcare-b79a9-default-rtdb.firebaseio.com/");
        firebaseStorage = FirebaseStorage.getInstance();
        myDatabaseRef=FirebaseDatabase.getInstance("https://animalcare-b79a9-default-rtdb.firebaseio.com/").getReference("ProfileData").child(firebaseAuth.getCurrentUser().getUid());
        selfhelpbtn=view.findViewById(R.id.selfhelpbtn);
        uploadImage=view.findViewById(R.id.uploadImage);
        spinner = view.findViewById(R.id.spinner);
        spinner2 = view.findViewById(R.id.city_spinner);

        sliderDotspanel = (LinearLayout) view.findViewById(R.id.SliderDots);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);

       // images.add(R.drawable.image_1);
        //images.add(R.drawable.image_2);

        myCustomPagerAdapter = new MyCustomPagerAdapter(getContext(), imagebitmap);
        viewPager.setAdapter(myCustomPagerAdapter);
        dotscount = myCustomPagerAdapter.getCount();





        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
               // images.add(R.drawable.image_3);
                getImageFromCamera();


            }
        });


        selfhelpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(new Intent(getActivity(),MapActivity.class));
                startActivity(intent);
                getActivity();

            }
        });

        getAnimalList();
        getCityList();







        final ImageButton locationBtn = view.findViewById(R.id.BtnGetLocation);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    showDialogforlocation("Location access", getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION);
                }
                else{
                    ActivityCompat
                            .requestPermissions(
                                    getActivity(),
                                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);

                    getLiveLocation();
                }
            }
        });


        viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
                }
                else{
                    getImageFromCamera();
                }
            }
        });

        getHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animalType = spinner.getSelectedItem().toString();
                cityType = spinner2.getSelectedItem().toString();


                if(userLocationTv.getText().toString().equals("Location Details")){
                    Toast.makeText(getContext(), "Please update your Live Location !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(animalType.equals("Select Animal")){
                    Toast.makeText(getContext(), "Please select Animal Type !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(cityType.equals("Select City")){
                    Toast.makeText(getContext(), "Please select City Type !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(imagebitmap==null || imagebitmap.size()==0){
                    Toast.makeText(getContext(), "Please capture the Image of injured Animal !", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!userLocationTv.getText().toString().equals("Locations Details") && !animalType.equals("Select Animal")&& !cityType.equals("Select city")) {

                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Getting help");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    uploadtasks.clear();
                    taskArrayList.clear();
                    imageurl.clear();
                    storageReferenceArrayList.clear();

                    int i;
                    Log.d("Bitmap size", "" + imagebitmap.size());
                    for (i = 0; i < imagebitmap.size(); i++) {
                        uniqueId = AnimalRescueUtil.generateAutoId();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        imagebitmap.get(i).compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream);
                        byte[] animalImageByteArray = byteArrayOutputStream.toByteArray();
                        final StorageReference storageReference = firebaseStorage.getReference("Animal Case Images").child(uniqueId);
                        UploadTask uploadTask = storageReference.putBytes(animalImageByteArray);
                        storageReferenceArrayList.add(storageReference);
                        uploadtasks.add(uploadTask);
//                        final int finalI = i;
//                        Log.d("I in loop",""+finalI);
//                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                                Toast.makeText(getContext(), "Image"+ finalI +" Uploading Successful !", Toast.LENGTH_SHORT).show();
//                                Log.d("UPLOADIMAGE","Image"+ finalI +" Uploading Successful");
//                                //task.getResult(
//                                storageReference.getDownloadUrl()
//                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                            @Override
//                                            public void onSuccess(Uri uri) {
//                                                url = uri.toString();
//                                                Log.d("URL in bitmap loop",url);
//                                                imageurl.add(url);
//                                                //if(finalI==imagebitmap.size()-1){
//                                                    //Log.d("IMAGEURL",imageurl.get(0)+imageurl.get(1));
//                                               // getProfileData();}
//
//                                            }
//                                        });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.cancel();
//                                Toast.makeText(getContext(), "Image Uploading Failed !", Toast.LENGTH_SHORT).show();
//                            }
//                        });

                    }
                    if(uploadtasks!=null && uploadtasks.size()>0){
                        whenAllSuccess(uploadtasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> objects) {
                                Log.d("STOREREF", "DONEDONE");
                                for (StorageReference storageRef : storageReferenceArrayList) {
                                    taskArrayList.add(storageRef.getDownloadUrl());
                                }
                                whenAllSuccess(taskArrayList).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                    @Override
                                    public void onSuccess(List<Object> objects) {
                                        Log.d("GETURLIMG", "OKOKOK");
                                        for (Object uriObject : objects) {
                                            Log.d("URLGOT", "true");
                                            imageurl.add(((Uri) uriObject).toString());
                                        }
                                        Toast.makeText(getContext(), "Image Uploading Done !", Toast.LENGTH_SHORT).show();
                                        getProfileData();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.cancel();
                                        Toast.makeText(getContext(), "Image Uploading Failed !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(getContext(), "Image Uploading Failed !", Toast.LENGTH_SHORT).show();
                            }
                        });
                }

                }
            }
        });
    }


    public void uploadData(String userName,String userNo){
        animalType = spinner.getSelectedItem().toString();
        cityType = spinner2.getSelectedItem().toString();

        if(!userLocationTv.getText().toString().equals("Location Details") && !userLocationTv.getText().toString().equals("")){
            if(location!=null && !animalType.equals("Select Animal") && !cityType.equals("Select City")) {
                //String UserName = firebaseAuth.getCurrentUser().getEmail();
                String userLocation = userLocationTv.getText().toString();
                String description=desc.getText().toString();
                double lat = location.getLatitude();
                double lng = location.getLongitude();
               final AnimalHelpCase helpCase = new AnimalHelpCase(userName,animalType,cityType,userLocation,lat,lng,imageurl,false,description);
               helpCase.setUserNo(userNo);
               if(firebaseUser!=null)
                helpCase.setUserUid(firebaseUser.getUid());
                helpCase.setRescueDocumentId(uniqueId);
                Log.d(TAG, "url: "+ url);
                db.collection("Cases").document("Topic").collection(cityType).document(uniqueId).set(helpCase).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            sendNotify(helpCase);
                            Toast.makeText(getContext(), "Case Created Successfully", Toast.LENGTH_SHORT).show();
                            Log.i("HELPCASE","Case Created Successfully");
                        }
                        else {
                            progressDialog.cancel();
                            Toast.makeText(getContext(), "Can't create a case", Toast.LENGTH_SHORT).show();
                            Log.i("HELPCASE","Can't create a case");
                        }

                    }
                });

            }
        }
    }

    public void getProfileData(){
        db.collection("ProfileData")
                .document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists() && documentSnapshot.get("name")!=null) {
                            Pname = documentSnapshot.get("name").toString();
                            Pno = documentSnapshot.get("no").toString();
                            uploadData(Pname,Pno);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(progressDialog.isShowing())
                    progressDialog.cancel();
                Toast.makeText(getContext(), "failed sending request", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void removedots()
    {
        int i;
        for( i = 0; i < dotscount; i++){

            //dots.add(new ImageView(getContext()));
        dots.get(i).setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.active_dot));
        dots.get(i).setAlpha(0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots.get(i), params);


        }
        dotscount=0;
        dots.clear();
    }
    public void displaydot()
    {
        int i;
        for( i = indexDot; i < dotscount; i++){

            dots.add(new ImageView(getContext()));
            dots.get(i).setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.non_active_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots.get(i), params);


        }
        indexDot=i;

        if(!dots.isEmpty()) {
            dots.get(0).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots.get(i).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.non_active_dot));
                }

                dots.get(position).setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void getCityList(){

        FirebaseFirestore ff= FirebaseFirestore.getInstance();
        CollectionReference cr= ff.collection("Cities");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null || value==null){
                    Toast.makeText(getContext(),"Error null City list",Toast.LENGTH_LONG).show();
                    return;
                }

                cityList.clear();
                cityList.add("Select City");


                for(DocumentSnapshot ds : value){
                    if(ds.exists())
                        cityList.add(ds.get("name").toString());
                }

                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,cityList);
                spinner2.setAdapter(arrayAdapter);
            }
        });
    }
    private void getAnimalList(){

        FirebaseFirestore ff= FirebaseFirestore.getInstance();
        CollectionReference cr= ff.collection("Animals");
        cr.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null || value==null){
                    Toast.makeText(getContext(),"Error null animal list",Toast.LENGTH_LONG).show();
                    return;
                }

                animalList.clear();
                animalList.add("Select Animal");
                animalList.add("Cat");
                animalList.add("Dog");
                animalList.add("Pigeon");
                animalList.add("Others");

                for(DocumentSnapshot ds : value){
                    if(ds.exists())
                    animalList.add(ds.get("name").toString());
                }

                ArrayAdapter<String> arrayAdapter
                        = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,animalList);
                spinner.setAdapter(arrayAdapter);
            }
        });
    }

}
