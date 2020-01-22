package com.example.microjobbd.fragment.workerfragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.microjobbd.R;
import com.example.microjobbd.worker.WorkerProfileUpdateActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import javax.xml.transform.Templates;

import de.hdodenhof.circleimageview.CircleImageView;


public class WorkerProfileFragment extends Fragment {

    TextView name,address,phone,nidNo,workertype;
    DatabaseReference databaseReference;
    String image;
    ImageButton edit;
    CircleImageView imageView;

    public WorkerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_worker_profile, container, false);

        name=view.findViewById(R.id.workerName);
        phone=view.findViewById(R.id.phoneNumber);
        address=view.findViewById(R.id.address);
        nidNo=view.findViewById(R.id.workerNIDNo);
        workertype=view.findViewById(R.id.workertype);
        imageView=view.findViewById(R.id.profile_image);
        edit=view.findViewById(R.id.edit);

        phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    if (d.getKey().equals("Full Name"))
                    {
                        name.setText(d.getValue().toString());
                    }
                    if (d.getKey().equals("Address"))
                    {
                        address.setText(d.getValue().toString());
                    }
                    if (d.getKey().equals("NID No"))
                    {
                        nidNo.setText(d.getValue().toString());
                    }
                    if (d.getKey().equals("Worker Type"))
                    {
                        workertype.setText(d.getValue().toString());
                    }
                    if (d.getKey().equals("Image")){
                        image=d.getValue().toString();
                        Log.d("Image",""+image);
                        Picasso.get().load(image).resize(400,400).centerCrop().into(imageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), WorkerProfileUpdateActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
