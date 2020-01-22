package com.example.microjobbd.worker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.microjobbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.microjobbd.R.color.colorAccent;

public class WorkerDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView name,address,phone,feedback,totalwork,rating;
    String workerNumber,image,imageworker;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,databaseReference3;
    Button request;
    CircleImageView profilePic;
    int n=0,i=0,r=0;
    double sumra=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);

        toolbar=findViewById(R.id.toolbar);
        name=findViewById(R.id.workerName);
        address=findViewById(R.id.address);
        phone=findViewById(R.id.phoneNumber);
        feedback=findViewById(R.id.feedback);
        totalwork=findViewById(R.id.totakworker);
        request=findViewById(R.id.requestBt);
        profilePic=findViewById(R.id.profilepic);
        rating=findViewById(R.id.rating);
        toolbar.setTitle("Worker Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        workerNumber=getIntent().getStringExtra("WorkerNumber");
        phone.setText(workerNumber);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Worker").child(workerNumber);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    if (d.getKey().equals("Full Name")){
                        name.setText(d.getValue().toString());
                    }
                    if (d.getKey().equals("Full address")){
                        address.setText(d.getValue().toString());
                    }
                    if (d.getKey().equals("Image")){
                        imageworker=d.getValue().toString();
                        Picasso.get().load(imageworker).resize(400,400).centerCrop().into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference3=FirebaseDatabase.getInstance().getReference().child("Rating").child(workerNumber);
        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d:dataSnapshot.getChildren()){
                    i++;
                    if (!d.child("Rating").getValue().toString().equals("0")){
                        sumra=sumra+Double.parseDouble(d.child("Rating").getValue().toString());
                        r++;
                    }
                }
                Log.d("Omar",""+r+"   "+sumra);
                String avgra=String.format("%.2f",sumra/r);
                rating.setText("Rating: "+avgra);
                totalwork.setText("Total Worke "+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @SuppressLint("ResourceAsColor")
    public void nextBt(View view) {
        Calendar cal = Calendar.getInstance();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(cal.getTime());
        if(n==0){
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Request").child(workerNumber);
        final DatabaseReference reference = databaseReference1.push();
        reference.child("Sender Phone No").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        reference.child("Request Time").setValue(currentDateandTime);
        reference.child("Is Seen").setValue("False");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals("Full Name")) {
                        reference.child("User Name").setValue(d.getValue().toString());
                    }
                    if (d.getKey().equals("Image")) {
                        image = d.getValue().toString();
                        reference.child("Image").setValue(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        request.setText("Already Requested");
        request.setBackgroundColor(colorAccent);
        request.setTextColor(colorAccent);
        n=1;
    }
    }
}
