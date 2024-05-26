package com.example.arcadeblastproject;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private Intent reg;
    private EditText firstName;
    private EditText lastName;
    private EditText username;
    private EditText password;
    private FirebaseFirestore db;
    private TextView fNameMessage;
    private TextView lNameMessage;
    private TextView uNameMessage;
    private TextView pMessage;
    private boolean[] isValid;
    private boolean isAvailable;
    private String uName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        reg = new Intent(this, SnakeMainMenu.class);
        firstName = findViewById(R.id.firstNameRegister);
        lastName = findViewById(R.id.lastNameRegister);
        username = findViewById(R.id.usernameRegister);
        password = findViewById(R.id.passwordRegister);
        fNameMessage = findViewById(R.id.fNameInvalid);
        lNameMessage = findViewById(R.id.lNameInvalid);
        uNameMessage = findViewById(R.id.uNameInvalid);
        pMessage = findViewById(R.id.pInvalid);
        db = FirebaseFirestore.getInstance();

        isValid = new boolean[4];
        for (int i = 0; i < 4; i++){
            isValid[i] = false;
        }

        firstName.setOnFocusChangeListener(focusListenerF);
        lastName.setOnFocusChangeListener(focusListenerL);
        username.setOnFocusChangeListener(focusListenerU);
        password.setOnFocusChangeListener(focusListenerP);
        firstName.addTextChangedListener(textWatcherF);
        lastName.addTextChangedListener(textWatcherL);
        username.addTextChangedListener(textWatcherU);
        password.addTextChangedListener(textWatcherP);
    }

    private View.OnFocusChangeListener focusListenerF = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (firstName.getText().toString().isEmpty()) {
                    isValid[0] = false;
                    fNameMessage.setVisibility(View.VISIBLE);
                    fNameMessage.setText("Please enter your first name");
                }
                else {
                    isValid[0] = true;
                }
            }
            else if (fNameMessage.getVisibility() == View.VISIBLE){
                fNameMessage.setVisibility(View.GONE);
            }
        }
    };

    private View.OnFocusChangeListener focusListenerL = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (lastName.getText().toString().isEmpty()) {
                    isValid[1] = false;
                    lNameMessage.setVisibility(View.VISIBLE);
                    lNameMessage.setText("Please enter your last name");
                }
                else {
                    isValid[1] = true;
                }
            }
            else if (lNameMessage.getVisibility() == View.VISIBLE){
                lNameMessage.setVisibility(View.GONE);
            }
        }
    };

    private View.OnFocusChangeListener focusListenerU = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (username.getText().toString().length() < 4) {
                    isValid[2] = false;
                    uNameMessage.setVisibility(View.VISIBLE);
                    uNameMessage.setText("Username too short, please make it longer");
                }
                else if (username.getText().toString().length() > 16){
                    isValid[2] = false;
                    uNameMessage.setVisibility(View.VISIBLE);
                    uNameMessage.setText("Username too long, please make it shorter");
                }
                else {
                    isValid[2] = true;
                }
            }
            else if (uNameMessage.getVisibility() == View.VISIBLE){
                uNameMessage.setVisibility(View.GONE);
            }
        }
    };

    private View.OnFocusChangeListener focusListenerP = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                if (password.getText().toString().length() < 8) {
                    isValid[3] = false;
                    pMessage.setVisibility(View.VISIBLE);
                    pMessage.setText("Your password must contain at least 8 characters");
                }
                else {
                    isValid[3] = true;
                }
            }
            else if (pMessage.getVisibility() == View.VISIBLE){
                pMessage.setVisibility(View.GONE);
            }
        }
    };

    private TextWatcher textWatcherF = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (firstName.getText().toString().isEmpty()) {
                isValid[0] = false;
                fNameMessage.setVisibility(View.VISIBLE);
                fNameMessage.setText("Please enter your first name");
            }
            else if (!hasNumbers(firstName.getText().toString())){
                isValid[0] = false;
                fNameMessage.setVisibility(View.VISIBLE);
                fNameMessage.setText("Please enter a valid first name");
            }
            else {
                isValid[0] = true;
                fNameMessage.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher textWatcherL = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (lastName.getText().toString().isEmpty()) {
                isValid[1] = false;
                lNameMessage.setVisibility(View.VISIBLE);
                lNameMessage.setText("Please enter your last name");
            }
            else if (!hasNumbers(lastName.getText().toString())){
                isValid[1] = false;
                lNameMessage.setVisibility(View.VISIBLE);
                lNameMessage.setText("Please enter a valid last name");
            }
            else {
                isValid[1] = true;
                lNameMessage.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher textWatcherU = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (username.getText().toString().length() < 4) {
                isValid[2] = false;
                uNameMessage.setVisibility(View.VISIBLE);
                uNameMessage.setText("Username too short, please make it longer");
            }
            else if (username.getText().toString().length() > 16){
                isValid[2] = false;
                uNameMessage.setVisibility(View.VISIBLE);
                uNameMessage.setText("Username too long, please make it shorter");
            }
            else {
                //availableName(username.getText().toString());
                uName = username.getText().toString();
                Query q = db.collection("users");
                q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Iterator<QueryDocumentSnapshot> it = task.getResult().iterator();
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                isAvailable = true;
                                String s = Objects.requireNonNull(documentSnapshots.get("username")).toString();
                                if (s.equals(uName)){
                                    isAvailable = false;
                                }

                                if (isAvailable){
                                    uNameMessage.setVisibility(View.GONE);
                                    isValid[2] = true;
                                } else {
                                    isValid[2] = false;
                                    uNameMessage.setVisibility(View.VISIBLE);
                                    uNameMessage.setText("Username is taken, please choose another name");
                                }
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private TextWatcher textWatcherP = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (password.getText().toString().length() < 8) {
                isValid[3] = false;
                pMessage.setVisibility(View.VISIBLE);
                pMessage.setText("Your password must contain at least 8 characters");
            }
            else if (aToZ()){
                pMessage.setVisibility(View.GONE);
                isValid[3] = true;
            }
            else {
                isValid[3] = false;
                pMessage.setVisibility(View.VISIBLE);
                pMessage.setText("Your password must both letters and numbers");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void register(View view) {

        HashMap<String, Object> user = new HashMap<>();

        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String uName = username.getText().toString();
        String pass = password.getText().toString();

        if(isValid()) {

            User u = new User(fName, lName, uName, pass, 0, 0);

            user.put("firstName", u.getFirstName());
            user.put("lastName", u.getLastName());
            user.put("username", u.getUsername());
            user.put("password", u.getPassword());
            user.put("highestScore", 0);
            user.put("survivalHighScore", 0);
            //user.put("profilePicture", u.getProfilePic());
            StorageReference defaultPic = FirebaseStorage.getInstance().getReference();
            Uri uri = getUriToDrawable(this, R.drawable.default_profile_pic);
            StorageReference imageReference = defaultPic.child(u.getUsername() + "/profilePicture");
            imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });

            db.collection("users").document(u.getUsername()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Register.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this, "There was a problem, please try again.\n" +
                            "Sorry for the inconvenience", Toast.LENGTH_SHORT).show();
                }
            });

            Login.setActiveUserName(u.getUsername());

            startActivity(reg);
        }
    }

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
        return imageUri;
    }

    public boolean isValid(){
        for (int i = 0; i < 4; i++){
            if (!isValid[i])
                return false;
        }

        return true;
    }

    public boolean aToZ(){
        char[] chars = password.getText().toString().toCharArray();

        boolean flag1 = false;
        boolean flag2 = false;

        for(char c: chars){
            if (((int)c >= 'a' && (int)c <= 'z') || ((int)c >= 'A' && (int)c <= 'Z'))
                flag1 = true;
            if ((int)c >= '0' && (int)c <= '9')
                flag2 = true;
        }

        return flag1 == flag2;
    }

    public boolean hasNumbers(String input){
        char[] name = input.toCharArray();

        for (char c: name){
            if ((int)c >= '0' && (int)c <= '9'){
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(this, MainActivity.class);
        startActivity(back);
        super.onBackPressed();
    }

}