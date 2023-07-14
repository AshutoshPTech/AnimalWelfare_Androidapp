package com.Pie4u.animalcare;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.type.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class nearbyplacesdata extends AsyncTask<Object,String,String> {
    String googlePlacesData;
    GoogleMap gmap;
    String url;
    private  LatLng latLng;
    @Override
    protected String doInBackground(Object... objects) {
        gmap=(GoogleMap) objects[0];
        url=(String) objects[1];

        DownloadMapUrls downloadMapUrls=new DownloadMapUrls();
        try {
            googlePlacesData=downloadMapUrls.readsurl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject parentObject=new JSONObject(s);
            JSONArray resultarray=parentObject.getJSONArray("results");

            for(int i=0;i<resultarray.length();i++){
                JSONObject jsonObject=resultarray.getJSONObject(i);
                JSONObject locationobject=jsonObject.getJSONObject("geometry").getJSONObject("location");

                String latitude=locationobject.getString("lat");
                String longitude=locationobject.getString("lng");

                JSONObject nameobj=resultarray.getJSONObject(i);
                String name=nameobj.getString("name");
                //LatLng latLng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                com.google.android.gms.maps.model.LatLng latLng=new com.google.android.gms.maps.model.LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
                MarkerOptions markerOptions=new MarkerOptions();

                markerOptions.title(name);
                markerOptions.position(latLng);

                gmap.addMarker(markerOptions);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
