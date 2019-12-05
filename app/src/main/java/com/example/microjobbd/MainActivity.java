package com.example.microjobbd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.microjobbd.activity.PhoneNumberActivity;
import com.example.microjobbd.activity.SetupProfileActivity;
import com.example.microjobbd.user.UserMainActivity;
import com.example.microjobbd.worker.WorkerMainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference,databaseReference1;
    ConstraintLayout linearLayout;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onStart(){
        super.onStart();
        linearLayout = findViewById(R.id.mainactivity);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();


        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Network error!");
        dialog.setMessage("Check your network setting and try again.");
        dialog.setCancelable(false);


        // dialog.setCanceledOnTouchOutside(false);
        //thread for launching another activity
        final Thread thread = new Thread(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(1000);//starting new activity after waiting 2000 ms
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //network manager
                        ConnectivityManager cm = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                        if(!isConnectNetwork()){
                            slowNetSlowDialog();

                        }
                        else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                            if(firebaseUser!=null){
                                takeDisitionToStartAC();
                            }else{
                                Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
                                finish();
                                startActivity(intent);
                            }
                        }
                        else{
                            switch (networkInfo.getType()){
                                case TelephonyManager.NETWORK_TYPE_CDMA:
                                    //14-64 kbps
                                    slowNetSlowDialog();
                                    onStart();
                                    break;
                                case TelephonyManager.NETWORK_TYPE_EDGE:
                                    //50-100 kbps
                                    slowNetSlowDialog();
                                    onStart();
                                    break;
                                case TelephonyManager.NETWORK_TYPE_1xRTT:
                                    //50-100 kbps
                                    slowNetSlowDialog();
                                    onStart();
                                    break;
                                case TelephonyManager.NETWORK_TYPE_GPRS:
                                    //100 kbps
                                    slowNetSlowDialog();
                                    onStart();
                                    break;
                                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                                    slowNetSlowDialog();
                                    onStart();
                                    break;

                                default:
                                    if(firebaseUser!=null){
                                        takeDisitionToStartAC();
                                    }else{
                                        Intent intent = new Intent(MainActivity.this,PhoneNumberActivity.class);
                                        finish();
                                        startActivity(intent);
                                    }
                                    break;
                            }
                        }
                    }
                });
            }
        };
        thread.start();
    }

    private void slowNetSlowDialog(){
        final Dialog slowNetdialog = new Dialog(MainActivity.this);
        slowNetdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        slowNetdialog.setCancelable(false);

        slowNetdialog.setContentView(R.layout.slow_net_dialog);

        Button dialogButton = (Button) slowNetdialog.findViewById(R.id.btn_dialog);
        Button settingBt = slowNetdialog.findViewById(R.id.settingbt);
        ImageButton cancelIm = slowNetdialog.findViewById(R.id.cancelImageButton);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart();
                slowNetdialog.dismiss();
            }
        });
        settingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            }
        });

        cancelIm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slowNetdialog.dismiss();
                finish();
            }
        });

        slowNetdialog.show();
    }

    void takeDisitionToStartAC(){
        Log.d("RRRR","RRR="+firebaseUser.getPhoneNumber());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(auth.getCurrentUser().getPhoneNumber());//current user references
        //databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Admin").child(auth.getCurrentUser().getPhoneNumber());//current user references
        Log.d("KKKK","KKK="+databaseReference.getKey());
        //Log.d("LLLL","LLLL="+databaseReference1.getKey());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int count = (int) dataSnapshot.getChildrenCount();
                    if (count>=3 && linearLayout.getVisibility() == View.VISIBLE && dataSnapshot.hasChild("Full Name") && dataSnapshot.hasChild("Address")){
                        Intent intent = new Intent(MainActivity.this, UserMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                databaseReference.child("Token id").setValue(instanceIdResult.getToken());

                            }
                        });
                        finish();
                        startActivity(intent);

                    }
                    else if(linearLayout.getVisibility() == View.VISIBLE && !dataSnapshot.hasChild("Full Name") && !dataSnapshot.hasChild("Address")){
                        Intent intent = new Intent(MainActivity.this, SetupProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                }
                else
                {
                    checkAdmin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //network connection check
    public boolean isConnectNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    public void checkAdmin(){
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Admin").child(auth.getCurrentUser().getPhoneNumber());//current user references
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    int count = (int) dataSnapshot.getChildrenCount();
                    if (count>=3 && linearLayout.getVisibility() == View.VISIBLE && dataSnapshot.hasChild("Full Name") && dataSnapshot.hasChild("Address")){
                        Intent intent = new Intent(MainActivity.this, WorkerMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                databaseReference1.child("Token id").setValue(instanceIdResult.getToken());

                            }
                        });
                        finish();
                        startActivity(intent);

                    }
                    else if(linearLayout.getVisibility() == View.VISIBLE && !dataSnapshot.hasChild("Full Name") && !dataSnapshot.hasChild("Address")){
                        Intent intent = new Intent(MainActivity.this, SetupProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
