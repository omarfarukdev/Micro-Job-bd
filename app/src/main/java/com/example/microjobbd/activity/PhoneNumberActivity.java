package com.example.microjobbd.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.microjobbd.R;

public class PhoneNumberActivity extends AppCompatActivity {

    TextView countrycode;
    EditText phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        countrycode=findViewById(R.id.countrycode);
        phonenumber=findViewById(R.id.phoneNumber);
    }

    public void nextBt(View view) {

        String number=phonenumber.getText().toString().trim();
        if(number.isEmpty()||number.length()<10)
        {
            phonenumber.setError("Valid Number is required");
            phonenumber.requestFocus();
            return;
        }
        String phnNumber="+880"+number;
        Intent intent=new Intent(PhoneNumberActivity.this,PhoneNumberVerifyActivity.class);
        intent.putExtra("phonenumber",phnNumber);
        startActivity(intent);
    }
}
