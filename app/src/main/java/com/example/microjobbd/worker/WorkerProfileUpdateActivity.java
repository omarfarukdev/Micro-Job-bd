package com.example.microjobbd.worker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.microjobbd.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkerProfileUpdateActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText name,address;
    CircleImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile_update);

        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Profile Edit");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
