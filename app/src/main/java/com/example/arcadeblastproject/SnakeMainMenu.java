package com.example.arcadeblastproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class SnakeMainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static SnakeMainMenuFragment snakeMainMenuFragment = new SnakeMainMenuFragment();
    public static ProfileFragment profileFragment = new ProfileFragment();
    public static Settings settingsFragment = new Settings();
    public static SurvivalGameMainMenuFragment survivalGameMainMenuFragment = new SurvivalGameMainMenuFragment();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_main_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, survivalGameMainMenuFragment).commit();
        }

    }

    public void instructions(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_snake_instructions);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.create();
        dialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, snakeMainMenuFragment).commit();
        } else if (id == R.id.nav_profile){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, profileFragment).commit();
        } else if (id == R.id.nav_settings){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, settingsFragment).commit();
        } else if (id == R.id.nav_survival) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, survivalGameMainMenuFragment).commit();
        } else if (id == R.id.nav_logout) {
            Intent go = new Intent(this, Login.class);
            startActivity(go);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}