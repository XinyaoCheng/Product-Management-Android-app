package com.example.a82.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.a82.fragment.RepertoryFragment;
import com.example.a82.fragment.HomeFragment;
import com.example.a82.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout home_view,repertory_view,account_view;
    Button menu_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation);
        home_view = findViewById(R.id.home_linearlayout);
        repertory_view = findViewById(R.id.repertory_linearlayout);
        account_view = findViewById(R.id.account_linearlayout);
        menu_button = findViewById(R.id.menu_button);
        HomeFragment homeFragment = new HomeFragment();
        setFragment(homeFragment);


        //slide menu function
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                menu_button.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                menu_button.setVisibility(View.VISIBLE);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    menu_button.setVisibility(View.VISIBLE);

                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    menu_button.setVisibility(View.INVISIBLE);
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        
        home_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment homeFragment = new HomeFragment();
                setFragment(homeFragment);
            }
        });
        repertory_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RepertoryFragment repertoryFragment = new RepertoryFragment();
                setFragment(repertoryFragment);
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.home_fragmentView,fragment)
                .commit();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            menu_button.setVisibility(View.VISIBLE);

        }
    }
}