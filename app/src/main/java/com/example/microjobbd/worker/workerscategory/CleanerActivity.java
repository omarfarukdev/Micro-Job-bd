package com.example.microjobbd.worker.workerscategory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.microjobbd.R;
import com.example.microjobbd.adapter.ViewPagerAdapter;
import com.example.microjobbd.fragment.ListFragment;
import com.example.microjobbd.fragment.MapFragment;
import com.google.android.material.tabs.TabLayout;

public class CleanerActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Cleaner");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tabLayout= (TabLayout) findViewById(R.id.tablayout);
        viewPager= (ViewPager) findViewById(R.id.viewpager);

       /* Log.d("latitude",""+getIntent().getStringExtra("latitude"));
        //Log.d("longitude",""+getIntent().getStringExtra("longitude"));
        bundle =new Bundle();
        bundle.putString("cleaner","Cleaner");
        MapFragment mapFragment=new MapFragment();
        mapFragment.setArguments(bundle);
        ListFragment listFragment=new ListFragment();
        listFragment.setArguments(bundle);*/

        SharedPreferences.Editor editor = this.getSharedPreferences("OMAR", MODE_PRIVATE).edit();
        editor.putString("cleaner", "Cleaner");
        editor.apply();

        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new MapFragment(),"Map View");
        adapter.AddFragment(new ListFragment(),"List View");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }
}
