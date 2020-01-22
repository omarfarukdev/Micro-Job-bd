package com.example.microjobbd.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.Rating;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.example.microjobbd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConfirmActivity extends AppCompatActivity {

    RatingBar ratingBar;
    EditText amount,feedback;
    Button done;
    Toolbar toolbar;
    String currentUser,workerNo;
    DatabaseReference databaseReference;
    int n=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        ratingBar=findViewById(R.id.ratingBar);
        amount=findViewById(R.id.amount);
        feedback=findViewById(R.id.feedback);
        done=findViewById(R.id.doneBt);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Feedback");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        workerNo=getIntent().getStringExtra("workerNumber");
        currentUser= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        Calendar cal = Calendar.getInstance();
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        final String currentDateandTime = sdf.format(cal.getTime());


            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (n==0) {
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Rating").child(workerNo).push();
                        databaseReference.child("User Phone No").setValue(currentUser);
                        databaseReference.child("FeedBack").setValue(feedback.getText().toString());
                        databaseReference.child("Rating").setValue(ratingBar.getRating());
                        databaseReference.child("Time").setValue(currentDateandTime);
                        databaseReference.child("Amount").setValue(amount.getText().toString());
                        n = 1;
                    }
                }
            });

    }
}
