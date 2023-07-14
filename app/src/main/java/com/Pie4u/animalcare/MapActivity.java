package com.Pie4u.animalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private GoogleMap gMap;
    private GoogleApi googleApiClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private Location mlastLocation;
    private LocationCallback locationCallback;
    private View MapView;
    private final float DEFAULT_ZOOM = 18;
    private Button findClinicBtn;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0X1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0X2;
    private  double currentLatitude;
    private  double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        findClinicBtn = findViewById(R.id.seachClinicbtn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.selphelpmap);
        mapFragment.getMapAsync(this);
        MapView=mapFragment.getView();
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient( MapActivity.this);
        Places.initialize(MapActivity.this,"AIzaSyDyFCZX4gauYeB5r6lRJPaLXqtwOUvYLpk");
        placesClient=Places.createClient(this);
        AutocompleteSessionToken token=AutocompleteSessionToken.newInstance();



    }

    private void setupclient() {
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient( MapActivity.this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
       ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);

        if(MapView!=null && MapView.findViewById(Integer.parseInt("1"))!=null){
            View loactionButton=((View) MapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams) loactionButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,180);
        }
        // check if location is enabled
        LocationRequest locationRequest=LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task=settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvable=(ResolvableApiException) e;
                    try {
                        //just used 51 anything can be used
                        resolvable.startResolutionForResult( MapActivity.this,51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(requestCode==51){
//            if(resultCode==RESULT_OK){
//                getDeviceLocation();
//            }
//        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful()){
                        mlastLocation=task.getResult();
                        if(mlastLocation!=null){
                            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlastLocation.getLatitude() ,mlastLocation.getLongitude()),DEFAULT_ZOOM));

                        }
                        else {
                            final LocationRequest locationRequest=LocationRequest.create();
                            locationRequest.setInterval(1000);
                            locationRequest.setFastestInterval(5000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            locationCallback=new LocationCallback(){
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    if(locationResult==null){
                                        return;
                                    }
                                    mlastLocation=locationResult.getLastLocation();
                                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlastLocation.getLatitude() ,mlastLocation.getLongitude()),DEFAULT_ZOOM));
                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                }
                            };
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
                        }
                    }else {
                        Toast.makeText(MapActivity.this,"Unable to get last location",Toast.LENGTH_LONG).show();
                    }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            checkPermission();

    }

    private void checkPermission() {
        int permissionlocation= ContextCompat.checkSelfPermission(MapActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);

        List<String>  listpermission=new ArrayList<>();
        if(permissionlocation !=PackageManager.PERMISSION_GRANTED){
            listpermission.add(Manifest.permission.ACCESS_FINE_LOCATION);

            if(listpermission.isEmpty()){
                ActivityCompat.requestPermissions(MapActivity.this,listpermission.toArray(new String[listpermission.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }
        else {
            getDeviceLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionlocation=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionlocation==PackageManager.PERMISSION_GRANTED){
            getDeviceLocation();

        }else {
            checkPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
            mlastLocation=location;
            if(mlastLocation!=null){

                currentLatitude=location.getLatitude();
                currentLongitude = location.getLatitude();

                //BitmapDescriptor icon= BitmapDescriptorFactory.fromResource(R.drawable.moti);

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude,currentLongitude) ,15.0f));

                MarkerOptions markerOptions=new MarkerOptions();
                markerOptions.position(new LatLng(currentLatitude,currentLongitude))
                        .title("you");
                gMap.addMarker(markerOptions);
                
                getNearbyClinics();
            }
    }

    private void getNearbyClinics() {
        StringBuilder stringBuilder=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+String.valueOf(currentLatitude)+","+String.valueOf(currentLongitude));
        stringBuilder.append("&radius=5000");
        stringBuilder.append("&type=hospital");
        stringBuilder.append("&key="+getResources().getString(R.string.google_maps_key));

        String url=stringBuilder.toString();
        Object dataTransfer[]= new Object[2];
        dataTransfer[0]=gMap;
        dataTransfer[1]=url;
        nearbyplacesdata placesdata=new nearbyplacesdata();
        placesdata.execute(dataTransfer);

    }
//    private void getMyLocation(){
//        if(googleApiClient!=null) {
//            if (googleApiClient.isConnected()) {
//                int permissionLocation = ContextCompat.checkSelfPermission(MapActivity.this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//                    mlastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                    LocationRequest locationRequest = new LocationRequest();
//                    locationRequest.setInterval(1000);
//                    locationRequest.setFastestInterval(1000);
//                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                            .addLocationRequest(locationRequest);
//                    builder.setAlwaysShow(true);
//                    LocationServices.FusedLocationApi
//                            .requestLocationUpdates(googleApiClient, locationRequest, this);
//                    PendingResult<LocationSettingsResult> result =
//                            LocationServices.SettingsApi
//                                    .checkLocationSettings(googleApiClient, builder.build());
//                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//
//                        @Override
//                        public void onResult(LocationSettingsResult result) {
//                            final Status status = result.getStatus();
//                            switch (status.getStatusCode()) {
//                                case LocationSettingsStatusCodes.SUCCESS:
//                                    // All location settings are satisfied.
//                                    // You can initialize location requests here.
//                                    int permissionLocation = ContextCompat
//                                            .checkSelfPermission(MapActivity.this,
//                                                    Manifest.permission.ACCESS_FINE_LOCATION);
//                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
//
//
//                                        mlastLocation = LocationServices.FusedLocationApi
//                                                .getLastLocation(googleApiClient);
//
//
//                                    }
//                                    break;
//                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                    // Location settings are not satisfied.
//                                    // But could be fixed by showing the user a dialog.
//                                    try {
//                                        // Show the dialog by calling startResolutionForResult(),
//                                        // and check the result in onActivityResult().
//                                        // Ask to turn on GPS automatically
//                                        status.startResolutionForResult(MapActivity.this,
//                                                REQUEST_CHECK_SETTINGS_GPS);
//
//
//                                    } catch (IntentSender.SendIntentException e) {
//                                        // Ignore the error.
//                                    }
//
//
//                                    break;
//                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                                    // Location settings are not satisfied.
//                                    // However, we have no way
//                                    // to fix the
//                                    // settings so we won't show the dialog.
//                                    // finish();
//                                    break;
//                            }
//                        }
//                    });
//
//                }
//            }
//        }
//    }
}