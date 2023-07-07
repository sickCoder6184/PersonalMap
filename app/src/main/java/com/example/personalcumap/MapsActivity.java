package com.example.personalcumap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.personalcumap.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private  String apiKey="AIzaSyCzqtRDUFOPOIpqWEvOnT0XW-bomcW2ZUY";
    private  GeoApiContext mGeoApiContext=null;
    SupportMapFragment mapFragment;

    private ActivityMapsBinding binding;

    //All Requests
    private final int ACCESS_LOCATION_REQUEST_CODE=1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    List<Place.Field> fields;
    String longitude="";
    String latitude="";


    SearchView searchView;
    AutoCompleteTextView SearchPlace;
    public final double Cu_Lat=30.77177672932136;
    public final double Cu_Lon=76.57820999706738;
    private LatLngBounds CuBonds;

    public BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);

    }


    //===========Restricting Map===========```
    private void setCameraView(){
        double bottomBoundary=30.764241091715235;
        double leftBoundary= 76.56759911118297;
        double topBoundary=30.772574767066377;
        double rightBoundary=76.58006601845803;

        CuBonds=new LatLngBounds(
                new LatLng(bottomBoundary,leftBoundary),
                new LatLng(topBoundary,rightBoundary)
        );

        mMap.setLatLngBoundsForCameraTarget(CuBonds);
    }
    //==============Restriction end========

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Set the fields to specify which types of place data to
        // return after the user has made a selection.

//        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);



        // OSM API starts
//
//        ArrayList<String> placesNamearr= new ArrayList<>();
//        RequestQueue requestQueue;
//        requestQueue= Volley.newRequestQueue(this);
//
//        JsonArrayRequest jsonArrayRequest= new JsonArrayRequest(Request.Method.GET,
//                "https://nominatim.openstreetmap.org/search?format=jsonv2&q={query}",
//                null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.d("AutoSuggestion","Successful response"+response.toString());
//
//                try {
//                     for (int i = 0; i < response.length(); i++) {
//                        JSONObject objres=response.getJSONObject(i);
//                        String placeName= objres.getString("display_name");
//                        placesNamearr.add(placeName);
//                        ArrayAdapter<String> arrAdap=new ArrayAdapter<String>
//                                (MapsActivity.this, android.R.layout.simple_list_item_1,placesNamearr);
//
//
//
//                    }
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("AutoSuggestion","Something went wrong");
//            }
//        });
//        requestQueue.add(jsonArrayRequest);


        // OSM API ends



        //=================Search View===============
        searchView=findViewById(R.id.sv_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location=searchView.getQuery().toString();
                List<Address> addressList=null;

                Geocoder geocoder=new Geocoder(MapsActivity.this);
                try {
                    addressList =geocoder.getFromLocationName(location,1);
                }catch (IOException e){
                    e.printStackTrace();
                }

                assert addressList != null;
                Address address=addressList.get(0);
                LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(latLng).title(location));//----->add marker to that area
                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(latLng,1);
                mMap.animateCamera(cameraUpdate, 2500, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(MapsActivity.this, " hi I am canceled ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(MapsActivity.this, "hey i am finished", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }


            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //==============Search View Ends=====================


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap!=null){
            setCameraView();
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==getPackageManager().PERMISSION_GRANTED) {
            enableuserlocation();

        }

        else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
            }
        }
            
       //MORE SETTINGS Compass
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setPadding(0,200,0,0);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions personalMarker = new MarkerOptions();
                personalMarker.position(latLng);

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                            stringBuilder.append(address.getAddressLine(i));
                            if (i < address.getMaxAddressLineIndex()) {
                                stringBuilder.append(", ");
                            }
                        }

                        personalMarker.title(stringBuilder.toString()).icon(getMarkerIcon("#ff2299"));
                        mMap.clear();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.addMarker(personalMarker);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



        LatLng perLoc= new LatLng(30.77177672932136, 76.57820999706738);
        mMap.addMarker(new MarkerOptions().position(perLoc).title("Our CU"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(perLoc));



    }





    private void enableuserlocation(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==ACCESS_LOCATION_REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==getPackageManager().PERMISSION_GRANTED){
                enableuserlocation();
            }
        }
    }


///===============Direction-========

//==========

}
