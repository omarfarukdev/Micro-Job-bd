package com.example.microjobbd.fragment.userfragments;

import android.content.Intent;
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
import com.example.microjobbd.user.UserProfileUpdateActivity;
import com.example.microjobbd.worker.WorkerProfileUpdateActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {

    TextView name,address,phone,nidNo;
    DatabaseReference databaseReference;
   ImageButton edit;
    String image;
    CircleImageView imageView;
    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_profile, container, false);
        name=view.findViewById(R.id.username);
        address=view.findViewById(R.id.address);
        phone=view.findViewById(R.id.phoneNumber);
        nidNo=view.findViewById(R.id.userNIDNo);
        imageView=view.findViewById(R.id.profile_image);
        edit=view.findViewById(R.id.edit);

        phone.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        databaseReference= FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
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
                    if (d.getKey().equals("Image")){
                        image=d.getValue().toString();
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
                Intent intent=new Intent(getActivity(), UserProfileUpdateActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
