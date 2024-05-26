package com.example.arcadeblastproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private Intent log;
    private FirebaseFirestore db;
    private EditText username;
    private EditText password;
    private SharedPreferences sharedPreferences;
    private RadioButton remember;
    private static String activeUserName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        log = new Intent(this, SnakeMainMenu.class);
        username = findViewById(R.id.lUsername);
        password = findViewById(R.id.lPassword);
        db = FirebaseFirestore.getInstance();
        remember = findViewById(R.id.rememberLogin);
        sharedPreferences = getSharedPreferences("details", 0);

        if (sharedPreferences.getString("username", null) != null){
            username.setText(sharedPreferences.getString("username", null));
            password.setText(sharedPreferences.getString("password", null));
        }
    }

    private void rememberMe(){
        if(remember.isChecked()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", username.getText().toString());
            editor.putString("password", password.getText().toString());
            editor.apply();

        }
        else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("password");
            editor.apply();
        }
    }

    public void login(View view) {

        Query q = db.collection("users").whereEqualTo("username", username.getText().toString())
                .whereEqualTo("password", password.getText().toString());

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(DocumentSnapshot b : task.getResult().getDocuments()){
                        if (Objects.equals(b.get("username"), username.getText().toString()) &&
                                Objects.equals(b.get("password"), password.getText().toString())){
                            Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(log);
                            rememberMe();
                            activeUserName = username.getText().toString();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
        super.onBackPressed();
    }

    public static String getActiveUserName() {
        return activeUserName;
    }

    public static void setActiveUserName(String activeUserName) {
        Login.activeUserName = activeUserName;
    }
}