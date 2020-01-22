package com.example.microjobbd.fragment.workerfragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.microjobbd.R;
import com.example.microjobbd.adapter.WorkerNotificationListAdapter;
import com.example.microjobbd.models.WorkerNotificationInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WorkerNotificationFragment extends Fragment {

    ListView listView;
    private WorkerNotificationListAdapter workerNotificationListAdapter;
    private ArrayList<WorkerNotificationInfo> notificationList;
    DatabaseReference databaseReference;

    public WorkerNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_worker_notification, container, false);
        listView=view.findViewById(R.id.notificationlist);
        notificationList=new ArrayList<>();
        workerNotificationListAdapter=new WorkerNotificationListAdapter(getActivity(),0,notificationList);

        try {
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Request").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        if (d.child("Is Seen").getValue().toString().equals("False")) {
                            Log.d("Key",""+d.getKey());
                            WorkerNotificationInfo workerNotificationInfo = new WorkerNotificationInfo(d.child("User Name").getValue().toString(), d.child("Sender Phone No").getValue().toString(), d.child("Request Time").getValue().toString(),d.getKey(),d.child("Image").getValue().toString());
                            notificationList.add(workerNotificationInfo);
                            listView.setAdapter(workerNotificationListAdapter);
                            workerNotificationListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){

        }

        return view;
    }


}
