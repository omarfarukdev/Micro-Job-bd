package com.example.microjobbd.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.microjobbd.R;
import com.example.microjobbd.user.UserMainActivity;
import com.example.microjobbd.worker.WorkerMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupProfileActivity extends AppCompatActivity {

    EditText userFullName,userAddress,userNIDno;
    Spinner gender,usertype;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        userAddress=findViewById(R.id.useraddressEt);
        userFullName=findViewById(R.id.userfullnameEt);
        userNIDno=findViewById(R.id.userNIDNoEt);
        gender=findViewById(R.id.genderSp);
        usertype=findViewById(R.id.userSp);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Setup Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(SetupProfileActivity.this, android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.gender));
        gender.setAdapter(genderAdapter);
        ArrayAdapter<String> usertypeAdapter=new ArrayAdapter<>(SetupProfileActivity.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.usertype));
        usertype.setAdapter(usertypeAdapter);
    }

    public void nextBt(View view) {

        int fName = userFullName.getText().toString().length();
        int faddress=userAddress.getText().toString().length();
        int fnid=userNIDno.getText().toString().length();
        String usertyp=usertype.getSelectedItem().toString();

        if(fName <= 3){
            userFullName.setError("Enter full name.");
        }
        else if(faddress<=6)
        {
            userAddress.setError("Please Your Addres");
        }
        else if(fnid<=9)
        {
            userNIDno.setError("Please Your NID");
        }
       /* else if (!usertyp.equals("User")||!usertyp.equals("Worker")){
            AlertDialog.Builder builder=new AlertDialog.Builder(SetupProfileActivity.this);
            builder.setMessage("Please select your uset type").setPositiveButton("Ok",null);
            AlertDialog alertDialog=builder.create();
            alertDialog.show();
        }*/

        else if(fName>3&&faddress>6&&fnid>9&&usertyp.equals("User"))
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());//current user database reference
            databaseReference.child("Full Name").setValue(userFullName.getText().toString());
            databaseReference.child("Address").setValue(userAddress.getText().toString());
            databaseReference.child("Room No").setValue(userNIDno.getText().toString());
            databaseReference.child("Gender").setValue(gender.getSelectedItem().toString());
            Intent intent=new Intent(SetupProfileActivity.this, UserMainActivity.class);
            SetupProfileActivity.this.finish();
            startActivity(intent);

        }
        else if(fName>3&&faddress>6&&fnid>9&&usertyp.equals("Worker"))
        {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Worker").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());//current user database reference
            databaseReference.child("Full Name").setValue(userFullName.getText().toString());
            databaseReference.child("Address").setValue(userAddress.getText().toString());
            databaseReference.child("Room No").setValue(userNIDno.getText().toString());
            databaseReference.child("Gender").setValue(gender.getSelectedItem().toString());
            Intent intent=new Intent(SetupProfileActivity.this, WorkerMainActivity.class);
            SetupProfileActivity.this.finish();
            startActivity(intent);

        }
    }
}
