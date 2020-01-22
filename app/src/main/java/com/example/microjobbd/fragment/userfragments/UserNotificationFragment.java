package com.example.microjobbd.fragment.userfragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.microjobbd.R;
import com.example.microjobbd.adapter.UserNotificationListAdapter;
import com.example.microjobbd.models.UserNotificationInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.microjobbd.R.color.colorPrimaryDark;


public class UserNotificationFragment extends Fragment {

    DatabaseReference databaseReference,databaseReference1;
    ListView listView;
    private UserNotificationListAdapter userNotificationListAdapter;
    private ArrayList<UserNotificationInfo> arrayList;
    String currentUser;

    public UserNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_notification, container, false);

        listView=view.findViewById(R.id.notificationlist);
        arrayList=new ArrayList<>();
        userNotificationListAdapter=new UserNotificationListAdapter(getActivity(),0,arrayList);

        try {
            currentUser= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Request Status").child(currentUser);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d:dataSnapshot.getChildren()) {
                            UserNotificationInfo userNotificationInfo=new UserNotificationInfo(d.child("Worker Name").getValue().toString(),d.child("Worker Phone No").getValue().toString(),d.child("Request Status").getValue().toString(),d.child("Time").getValue().toString(),d.child("Worker Image").getValue().toString(),d.getKey(),d.child("Is Seen").getValue().toString());
                            arrayList.add(userNotificationInfo);
                            listView.setAdapter(userNotificationListAdapter);
                            userNotificationListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }catch (Exception e){

        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (arrayList.get(position).getStatus().equals("Denied")&&arrayList.get(position).getIsSeen().equals("False")){
                    listView.setSelection(R.color. colorAccent ) ;
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot d:dataSnapshot.getChildren()) {
                                if (d.getKey().equals(arrayList.get(position).getKey())) {
                                    databaseReference1=databaseReference.child(d.getKey());
                                    databaseReference1.child("Is Seen").setValue("True");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


        return view;
    }
}
