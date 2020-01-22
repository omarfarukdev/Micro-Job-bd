package com.example.microjobbd.worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.microjobbd.R;
import com.example.microjobbd.fragment.userfragments.UserHomeFragment;
import com.example.microjobbd.fragment.userfragments.UserNotificationFragment;
import com.example.microjobbd.fragment.userfragments.UserProfileFragment;
import com.example.microjobbd.fragment.workerfragments.WorkerNotificationFragment;
import com.example.microjobbd.fragment.workerfragments.WorkerProfileFragment;
import com.example.microjobbd.service.LocationService;
import com.example.microjobbd.user.UserMainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class WorkerMainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    private WorkerNotificationFragment workerNotificationFragment;
    private WorkerProfileFragment workerProfileFragment;
    private FusedLocationProviderClient fusedLocationClient;
    double latitude,longitude;
    private String fulladdress;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        workerNotificationFragment=new WorkerNotificationFragment();
        workerProfileFragment =new WorkerProfileFragment();
        setFragment(workerNotificationFragment);

        bundle = new Bundle();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.worker_notification_nav:
                        setFragment(workerNotificationFragment);
                        break;
                    case R.id.worker_profile:
                        setFragment(workerProfileFragment);
                        break;

                    default:
                        break;
                }

                return true;
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);//fusedLocation provider client
        getLocation();
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        i.putExtra("currentuser","Worker");
        startService(i);

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10: {
                //getLocation();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // now, you have permission go ahead
                    // TODO: something
                    getLocation();

                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(WorkerMainActivity.this,
                            ACCESS_FINE_LOCATION)) {
                        // now, user has denied permission (but not permanently!)
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setMessage("We need your location to find out near donor. Please allow the permission");
                        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getLocation();
                            }
                        });
                        builder.show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false);
                        builder.setMessage("We need your location to find out near donor. Please allow the permission"+"\n\nSetting -> App permission -> Location");
                        builder.setPositiveButton("Go to setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                //getLocation();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        builder.show();
                    }
                }
                break;
            }
        }
    }
    public void getLocation()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //if (ActivityCompat.shouldShowRequestPermissionRationale())
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    // Logic to handle location object

                    Geocoder geocoder = new Geocoder(WorkerMainActivity.this, Locale.getDefault());
                    List<Address> addresses;
                    ArrayList<String> addressList = new ArrayList<>();

                    try{

                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Address obj = addresses.get(0);

                        fulladdress = obj.getAddressLine(0);
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        String[] sp = fulladdress.split(",");
                        for(int i=0;i<sp.length;i++){
                            addressList.add(sp[i]);
                        }
                        Log.d("Latitude",""+latitude);
                        Log.d("Longitude",""+longitude);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                        databaseReference.child("Latitude").setValue(latitude);
                        databaseReference.child("Longitude").setValue(longitude);


                    }
                    catch (Exception ex){

                    }


                }
            }
        });
    }
}
