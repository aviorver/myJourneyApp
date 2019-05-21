package com.example.guy.journeyblog;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String currentid;
    public static ArrayList<LatLng> listPoints=  new ArrayList<>();
    public static ArrayList<Float> listColors=  new ArrayList<>();
    public CollectionReference ref = firebaseFirestore.collection("MarkerLocations");
    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    private EditText mSearchText;
    private Spinner spinner;
    private float defaultColor ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = findViewById(R.id.input_search);
        mAuth = FirebaseAuth.getInstance();
        currentid = mAuth.getUid();
       //  Obtain the SupportMapFragment and get notified when the map is ready to be used.
         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.map);
           mapFragment.getMapAsync(this);

        String []spinnerText = {getString(R.string.colors),getString(R.string.blue),getString(R.string.red),getString(R.string.yellow),getString(R.string.green),getString(R.string.rose)
                ,getString(R.string.cyan)};
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerText){
            @Override
            public View getDropDownView(int position,View convertView,ViewGroup parent) {
                View row = super.getDropDownView(position, convertView, parent);
                switch (position) {
                    case 0 :
                        row.setBackgroundColor(Color.WHITE);
                        break;
                    case 1:
                        row.setBackgroundColor(Color.BLUE);
                        break;
                    case 2:
                        row.setBackgroundColor(Color.RED);
                        break;
                    case 3:
                        row.setBackgroundColor(Color.YELLOW);
                        break;
                    case 4:
                        row.setBackgroundColor(Color.GREEN);
                        break;
                    case 5:
                        row.setBackgroundColor(Color.MAGENTA);
                        break;
                    case 6:
                        row.setBackgroundColor(Color.CYAN);
                        break;
                }
                return(row);
            }
        };
        spinner.setAdapter(dataAdapter);
        if(listPoints!= null) {
            for (int i = 0; i < listPoints.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(listPoints.get(i)).icon(BitmapDescriptorFactory.defaultMarker(defaultColor));
                mMap.addMarker(markerOptions);

            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                switch (position) {
                    case 0:
                        spinner.setBackgroundColor(Color.WHITE);
                        break;
                    case 1:
                        defaultColor = BitmapDescriptorFactory.HUE_BLUE;
                        spinner.setBackgroundColor(Color.BLUE);
                        break;
                    case 2:
                        defaultColor = BitmapDescriptorFactory.HUE_RED;
                        spinner.setBackgroundColor(Color.RED);

                        break;
                    case 3:
                        defaultColor = BitmapDescriptorFactory.HUE_YELLOW;
                        spinner.setBackgroundColor(Color.YELLOW);
                        break;
                    case 4:
                        defaultColor = BitmapDescriptorFactory.HUE_GREEN;
                        spinner.setBackgroundColor(Color.GREEN);
                        break;
                    case 5:
                        defaultColor = BitmapDescriptorFactory.HUE_ROSE;
                        spinner.setBackgroundColor(Color.MAGENTA);
                        break;
                    case 6:
                        defaultColor = BitmapDescriptorFactory.HUE_CYAN;
                        spinner.setBackgroundColor(Color.CYAN);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    private void init(){

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == event.ACTION_DOWN || event.getAction() == event.KEYCODE_ENTER ) {
                    geoLocate();

                }
                return false;

            }
        });

    }

    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.e("", ""+e.getMessage());
        }

        if(list.size() > 0)
        {
            Address address = list.get(0);
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 10,
                    address.getAddressLine(0 ));
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        init();
        ref.document(currentid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    //MarkerLocations location = new MarkerLocations(documentSnapshot.toString().get("lastselected"), documentSnapshot.toString().getString("colors"), documentSnapshot.toString().getString("position"));
                    MarkerLocations markerLocations = documentSnapshot.toObject(MarkerLocations.class);
                    for (int i = 0; i < markerLocations.getPosition().size(); i = i + 2) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);

                        LatLng lng = new LatLng(markerLocations.getPosition().get(i), markerLocations.getPosition().get(i + 1));
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(lng).icon(BitmapDescriptorFactory.defaultMarker(markerLocations.getColors().get(i/2)));
                        mMap.addMarker(markerOptions);
                        listPoints.add(lng);
                        listColors.add(markerLocations.getColors().get(i/2));
                        spinner.setSelection(markerLocations.getLastselected());
                    }
                }
            }
        });

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if(spinner.getSelectedItemPosition()!=0) {

                     if((Double)mMap.getMyLocation().getLatitude() != null || (Double)mMap.getMyLocation().getLongitude()!=null) {
                        LatLng j = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(j).icon(BitmapDescriptorFactory.defaultMarker(defaultColor));
                        mMap.addMarker(markerOptions);
                        listPoints.add(j);
                        listColors.add(defaultColor);
                        writeToFireBase();
                    }
                    else
                         Toast.makeText(MapsActivity.this,getString(R.string.locationerror),Toast.LENGTH_LONG);
                }
                return false;
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(spinner.getSelectedItemPosition()!=0) {

                    listPoints.add(latLng);
                    //Create marker
                    listColors.add(defaultColor);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(defaultColor));
                    mMap.addMarker(markerOptions);

                    writeToFireBase();
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
               listColors.remove(listPoints.indexOf(marker.getPosition()));
                listPoints.remove(marker.getPosition());

                writeToFireBase();
                marker.showInfoWindow();
                return false;
            }
        });
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
    public void writeToFireBase(){
        ArrayList<Double> listPointsFireBase = new ArrayList<Double>();
        ArrayList<Float> listColorFireBase = new ArrayList<Float>();
        for(int i=0;i<listPoints.size() ; i++)
        {
            listPointsFireBase.add(listPoints.get(i).latitude);
            listPointsFireBase.add(listPoints.get(i).longitude);
            listColorFireBase.add(listColors.get(i));
        }


        Map<String,Object> map = new HashMap<>();
        map.put("lastselected",spinner.getSelectedItemPosition());
        map.put("colors",listColorFireBase);
        map.put("position",listPointsFireBase);

        ref.document(currentid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mMap.clear();
        listPoints.clear();
        listColors.clear();
        finish();
    }
}