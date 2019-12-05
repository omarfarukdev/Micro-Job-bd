package com.example.microjobbd.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.microjobbd.R;
import com.example.microjobbd.fragment.userfragments.UserHomeFragment;
import com.example.microjobbd.fragment.userfragments.UserNotificationFragment;
import com.example.microjobbd.fragment.userfragments.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserMainActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    private UserHomeFragment userHomeFragment;
    private UserNotificationFragment userNotificationFragment;
    private UserProfileFragment userProfileFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userHomeFragment=new UserHomeFragment();
        userNotificationFragment=new UserNotificationFragment();
        userProfileFragment=new UserProfileFragment();
        setFragment(userHomeFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.renter_home_nav:
                        setFragment(userHomeFragment);
                        break;
                    case R.id.renter_notification_nav:
                        setFragment(userNotificationFragment);
                        break;
                    case R.id.renter_profile_nav:
                       setFragment(userProfileFragment);
                        break;

                    default:
                        break;
                }

                return true;
            }
        });
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
