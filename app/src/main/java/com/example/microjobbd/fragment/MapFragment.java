package com.example.microjobbd.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.microjobbd.R;
import com.example.microjobbd.models.WorkerInformation;
import com.example.microjobbd.worker.WorkerDetailsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

import static android.content.Context.MODE_PRIVATE;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    private GoogleMap gmap;
    private DatabaseReference databaseReference, databaseReference2;
    String loadedString, workerphno;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;
    LatLng latLng;
    Location mylocation;
    double distance;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map, container, false);


       /* Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("KKK");
        }*/
         mylocation = new Location("locationA");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());//fusedLocation provider client

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.onStart();
        SharedPreferences prefs = getContext().getSharedPreferences("OMAR", MODE_PRIVATE);
        loadedString = prefs.getString("cleaner", null);
        Log.d("Map",""+loadedString);
        getCurrentLocation();
        return view;
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //if (ActivityCompat.shouldShowRequestPermissionRationale())
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);

            return;
        }
        /*fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    *//*gmap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .draggable(false).visible(true));*//*
                }
            }
        });*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle("KKK");
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle("KKK", mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;
        //LatLng ny = new LatLng(23.777176, 90.399452);


        /*LatLng latLng = new LatLng(23.8319, 90.4178);
        LatLng latLn = new LatLng(23.8759, 90.3795);*/
        if (gmap != null) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location!=null){
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mylocation.setLatitude(location.getLatitude());
                        mylocation.setLongitude(location.getLongitude());
                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
                    gmap.addMarker(new MarkerOptions()
                            .position(latLng).title("You")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .draggable(false).visible(true)).showInfoWindow();

                    }
                }
            });

            /*gmap.addMarker(new MarkerOptions()
                    .position(latLn).title("Uttara").snippet("tttttt")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                    .draggable(false).visible(true));*/

            //getCurrentLocation();
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Worker");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d:dataSnapshot.getChildren()){


                        Location dest_location = new Location("locationA");
                        //allWorker(d.getKey());

                        Log.d("Num",""+d.child("Address").getValue().toString());
                        Log.d("Type",""+d.child("Worker Type").getValue().toString());
                        if (d.child("Worker Type").getValue().toString().equals(loadedString.trim())&&d.child("Verification").getValue().toString().equals("Yes")){
                            workerphno=d.getKey();
                            Log.d("WorkerNum",""+workerphno);
                            //workerphno=d.getKey();
                            dest_location.setLatitude(Double.parseDouble(d.child("Latitude").getValue().toString()));
                            dest_location.setLongitude(Double.parseDouble(d.child("Longitude").getValue().toString()));

                            LatLng latLn = new LatLng(Double.parseDouble(d.child("Latitude").getValue().toString()), Double.parseDouble(d.child("Longitude").getValue().toString()));
                            distance = mylocation.distanceTo(dest_location);
                            Log.d("Dis",""+distance/1000);

                            if (distance/1000<=5){
                                gmap.addMarker(new MarkerOptions()
                                        .position(latLn).title(d.child("Full Name").getValue().toString()).snippet(workerphno)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                                        .draggable(false).visible(true)).showInfoWindow();
                            }

                       /* mylocation.setLatitude(Double.parseDouble(lati));
                        mylocation.setLongitude(Double.parseDouble(longt));

                        distance = mylocation.distanceTo(dest_location);
                        String dist=Float.toString(distance/1000)+" k";*/

                            //WorkerInformation workerInformation=new WorkerInformation(d.child("Full Name").getValue().toString(),dist,workerphno);
                      /*  workerlist.add(workerInformation);
                        listView.setAdapter(workerListAdapter);
                        workerListAdapter.notifyDataSetChanged();*/
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getTitle().equals("You")&&marker.getSnippet().length()>0){
                    Intent intent=new Intent(getActivity(), WorkerDetailsActivity.class);
                    intent.putExtra("WorkerNumber",marker.getSnippet());
                    startActivity(intent);
                }
                return false;
            }
        });
        mapView.onResume();
    }

}
