package com.example.microjobbd.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.microjobbd.R;
import com.example.microjobbd.models.WorkerNotificationInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerNotificationListAdapter extends ArrayAdapter<WorkerNotificationInfo> {

    ArrayList<WorkerNotificationInfo> arrayList;
    Context context;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,reference;
    String workerName,workerImage,currentNumber;

    public WorkerNotificationListAdapter(@NonNull Context context, int resource,ArrayList<WorkerNotificationInfo> arrayList) {
        super(context, resource,arrayList);
        this.arrayList=arrayList;
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= convertView;
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.worker_notification_shap,null);
        TextView name=view.findViewById(R.id.requesterName);
        TextView phoneNo=view.findViewById(R.id.requesterNo);
        TextView time=view.findViewById(R.id.requestTime);
        Button denied=view.findViewById(R.id.denideBtn);
        Button accept=view.findViewById(R.id.acceptBtn);
        CircleImageView requestImage=view.findViewById(R.id.requestImage);
        name.setText(arrayList.get(position).getName());
        phoneNo.setText(arrayList.get(position).getPhoneNumber());
        time.setText(arrayList.get(position).getRequestTime());
        Picasso.get().load(arrayList.get(position).getImage()).resize(400,400).centerCrop().into(requestImage);

        Calendar cal = Calendar.getInstance();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String currentDateandTime = sdf.format(cal.getTime());

        currentNumber=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        reference=FirebaseDatabase.getInstance().getReference().child("Worker").child(currentNumber);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    if (d.getKey().equals("Full Name"))
                    {
                        workerName=d.getValue().toString();
                    }
                    if (d.getKey().equals("Image")){
                        workerImage=d.getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request").child(currentNumber);
        denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Attention");
                dialog.setMessage("Sure to deny this request ?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot d:dataSnapshot.getChildren()) {
                                    if (d.getKey().equals(arrayList.get(position).getKey())) {
                                        Log.d("omar",""+arrayList.get(position).getKey());
                                        databaseReference1=databaseReference.child(d.getKey());
                                        databaseReference1.child("Is Seen").setValue("True");

                                        databaseReference2=FirebaseDatabase.getInstance().getReference().child("Request Status").child(arrayList.get(position).getPhoneNumber()).push();
                                        databaseReference2.child("Is Seen").setValue("False");
                                        databaseReference2.child("Time").setValue(currentDateandTime);
                                        databaseReference2.child("Request Status").setValue("Denied");
                                        databaseReference2.child("Worker Phone No").setValue(currentNumber);
                                        databaseReference2.child("Worker Name").setValue(workerName);
                                        databaseReference2.child("Worker Image").setValue(workerImage);
                                        arrayList.remove(getItem(position));
                                        break;
                                    }
                                }
                                Toast.makeText(getContext(), "Request deny.", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot d:dataSnapshot.getChildren()) {
                            if (d.getKey().equals(arrayList.get(position).getKey())) {
                                Log.d("omar",""+arrayList.get(position).getKey());
                                databaseReference1=databaseReference.child(d.getKey());
                                databaseReference1.child("Is Seen").setValue("True");

                                databaseReference2=FirebaseDatabase.getInstance().getReference().child("Request Status").child(arrayList.get(position).getPhoneNumber()).push();
                                databaseReference2.child("Is Seen").setValue("False");
                                databaseReference2.child("Time").setValue(currentDateandTime);
                                databaseReference2.child("Request Status").setValue("Accept");
                                databaseReference2.child("Worker Phone No").setValue(currentNumber);
                                databaseReference2.child("Worker Name").setValue(workerName);
                                databaseReference2.child("Worker Image").setValue(workerImage);
                                arrayList.remove(getItem(position));
                                break;
                            }
                        }
                        Toast.makeText(getContext(), "Request accept.", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        return view;
    }
}
