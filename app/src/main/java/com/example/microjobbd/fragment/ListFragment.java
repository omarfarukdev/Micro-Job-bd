package com.example.microjobbd.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.microjobbd.R;
import com.example.microjobbd.adapter.WorkerListAdapter;
import com.example.microjobbd.models.WorkerInformation;
import com.example.microjobbd.worker.WorkerDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class ListFragment extends Fragment {

    private DatabaseReference databaseReference,databaseReference2;
    ArrayList<WorkerInformation> workerlist;
    private WorkerListAdapter workerListAdapter;
    ListView listView;
    String loadedString,lati,longt,workerphno;
    float distance;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_list, container, false);
       // View view1 = inflater.inflate(R.layout.activity_cleaner, container, false);
        /*LinearLayout linearLayout = view1.findViewById(R.id.cleanerActivity);
        if (linearLayout.getVisibility() == View.VISIBLE){
            Log.d("vvvv","true");
        }*/

        listView=view.findViewById(R.id.workerlist);
        workerlist=new ArrayList<>();
        workerListAdapter=new WorkerListAdapter(getActivity(),0,workerlist);

        SharedPreferences prefs = getContext().getSharedPreferences("OMAR", MODE_PRIVATE);
        loadedString = prefs.getString("cleaner", null);
        Log.d("List",""+loadedString);
        Log.d("slsk",""+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        try {
             databaseReference2 = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            databaseReference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        if(d.getKey().equals("Latitude")){
                            lati=d.getValue().toString();
                        }
                        if (d.getKey().equals("Longitude")){
                            longt=d.getValue().toString();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception ex){

        }

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Worker");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){

                    Location mylocation = new Location("locationA");
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
                        mylocation.setLatitude(Double.parseDouble(lati));
                        mylocation.setLongitude(Double.parseDouble(longt));

                        distance = mylocation.distanceTo(dest_location);
                       // String dist=Float.toString(distance/1000)+" k Way";
                        String dist=String.format("%.2f k Way",distance/1000);
                        if (distance/1000<=5) {
                            WorkerInformation workerInformation = new WorkerInformation(d.child("Full Name").getValue().toString(), dist, workerphno);
                            workerlist.add(workerInformation);
                            listView.setAdapter(workerListAdapter);
                            workerListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getActivity(), WorkerDetailsActivity.class);
                intent.putExtra("WorkerNumber",workerlist.get(position).getWorkerNum());
                startActivity(intent);
            }
        });

        return view;
    }

    public void allWorker(String worker){

        databaseReference2=FirebaseDatabase.getInstance().getReference().child("Worker").child(worker);
        Log.d("slsk",""+ FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                  //Log.d("Type",""+d.child("Worker Type").getValue().toString());
                    try {
                        Log.d("Worker",""+d.child("Worker Type").getValue().toString());
                        Log.d("Liss",""+loadedString);
                        if (d.child("Worker Type").getValue().toString().equals(loadedString.trim())){
                            WorkerInformation workerInformation=new WorkerInformation(d.child("Full Name").getValue().toString(),d.child("Full address").getValue().toString(),workerphno);
                            workerlist.add(workerInformation);
                            listView.setAdapter(workerListAdapter);
                            workerListAdapter.notifyDataSetChanged();
                        }
                    }
                    catch (Exception e){

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
