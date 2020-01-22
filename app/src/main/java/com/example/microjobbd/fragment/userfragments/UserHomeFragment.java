package com.example.microjobbd.fragment.userfragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.microjobbd.R;
import com.example.microjobbd.user.UserMainActivity;
import com.example.microjobbd.worker.workerscategory.BuaActivity;
import com.example.microjobbd.worker.workerscategory.CleanerActivity;
import com.example.microjobbd.worker.workerscategory.ElectricianActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;


public class UserHomeFragment extends Fragment {

    CardView cleaner,bua,electrician;
    String latitude,longitude;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_home, container, false);

        cleaner=view.findViewById(R.id.cleaner);
        bua=view.findViewById(R.id.bua);
        electrician=view.findViewById(R.id.electrician);


        Log.d("Latitude",""+latitude);
        Log.d("Longitude",""+longitude);
        cleaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CleanerActivity.class);
                intent.putExtra("latitude",latitude+"  "+longitude);
                //intent.putExtra("longtude",longitude);
                startActivity(intent);
            }
        });
        bua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), BuaActivity.class);
                intent.putExtra("latitude",latitude+"  "+longitude);
                //intent.putExtra("longtude",longitude);
                startActivity(intent);
            }
        });
        electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ElectricianActivity.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longtude",longitude);
                startActivity(intent);
            }
        });

        return view;
    }
}
