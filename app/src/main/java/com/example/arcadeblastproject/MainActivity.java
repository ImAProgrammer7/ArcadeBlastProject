package com.example.arcadeblastproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 200;
    private static final int REQUEST_STORAGE = 300;
    private static final int REQUEST_GROUP_PERMISSION = 500;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 41;

    private Intent regScreen;
    private Intent logScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regScreen = new Intent(this, Register.class);
        logScreen = new Intent(this, Login.class);

        ArrayList<String> permission = new ArrayList<>();
        permission.add(Manifest.permission.CAMERA);
        permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        requestGroupPermissions(permission);


    }

    public void registerScreen(View view) {
        startActivity(regScreen);
    }

    public void loginScreen(View view) {
        startActivity(logScreen);
    }

    public void requestGroupPermissions(ArrayList<String> permissions){
        if (permissions != null && permissions.size()>0) {
            String[] permissionGroups = new String[permissions.size()];
            permissions.toArray(permissionGroups);
            ActivityCompat.requestPermissions(MainActivity.this, permissionGroups, REQUEST_GROUP_PERMISSION);
        } else
            Toast.makeText(this,
                    "You have permission to camera, storage", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_CAMERA == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You have camera permission", Toast.LENGTH_SHORT).show();

                AlertDialog d = new AlertDialog.Builder(this)
                        .setTitle("run time permission").setMessage("you have camera permission")
                        .create();
                d.show();

            } else {
                Toast.makeText(this, "You Do not have camera permission", Toast.LENGTH_SHORT).show();
            }
        } else if (REQUEST_STORAGE == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You have storage permission", Toast.LENGTH_SHORT).show();
                AlertDialog d = new AlertDialog.Builder(this)
                        .setTitle("run time permission").setMessage("you have storage permission")
                        .create();
                d.show();

            } else {
                Toast.makeText(this, "You Do not have storage permission", Toast.LENGTH_SHORT).show();
            }

        } else if (READ_EXTERNAL_STORAGE_REQUEST_CODE == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You have gallery permission", Toast.LENGTH_SHORT).show();
                AlertDialog d = new AlertDialog.Builder(this)
                        .setTitle("run time permission").setMessage("you have storage permission")
                        .create();
                d.show();

            } else {
                Toast.makeText(this, "You Do not have gallery permission",Toast.LENGTH_SHORT).show();
            }
        }
    }
}