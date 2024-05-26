package com.example.arcadeblastproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class Leaderboard extends AppCompatActivity {

    private FirebaseFirestore db;
    private ListView leaderboard;
    private UserAdapter userAdapter;
    private ArrayList<User> users;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        leaderboard = findViewById(R.id.leaderboardList);
        users = new ArrayList<>();

        Query q = db.collection("users").orderBy("highestScore", Query.Direction.DESCENDING);
        q.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    Iterator<QueryDocumentSnapshot> iterator = value.iterator();
                    while (iterator.hasNext()){
                        users.add(iterator.next().toObject(User.class));
                    }

                    userAdapter = new UserAdapter(Leaderboard.this, 0, 0, users, "snake");
                    leaderboard.setAdapter(userAdapter);
                }
            }
        });

        leaderboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog dialog = new Dialog(Leaderboard.this);
                dialog.setContentView(R.layout.fragment_other_people_profile);
                int height = getResources().getDisplayMetrics().heightPixels;
                int width = getResources().getDisplayMetrics().widthPixels;
                dialog.getWindow().setLayout(width - 300, height - 300);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.create();
                dialog.show();
                TextView tvFirstName = dialog.getWindow().findViewById(R.id.firstNameField);
                TextView tvLastName = dialog.getWindow().findViewById(R.id.lastNameField);
                TextView tvUsername = dialog.getWindow().findViewById(R.id.usernameField);
                TextView tvSnakeHighestScore = dialog.getWindow().findViewById(R.id.snakeHighScoreField);
                TextView tvSurvivalHighestScore = dialog.getWindow().findViewById(R.id.survivalHighScoreField);
                CircleImageView ibProfilePicture = dialog.getWindow().findViewById(R.id.profilePicture);
                StorageReference imageReference = storageReference.child(userAdapter.getItem(i).getUsername() + "/profilePicture");
                final int MAX_BYTES = 1024 * 1024;
                imageReference.getBytes(MAX_BYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ibProfilePicture.setImageBitmap(bitmap);
                    }
                });
                tvFirstName.setText("First Name: " + userAdapter.getItem(i).getFirstName());
                tvLastName.setText("Last Name: " + userAdapter.getItem(i).getLastName());
                tvUsername.setText("Username: " + userAdapter.getItem(i).getUsername());
                tvSnakeHighestScore.setText("Snake Highest Score: " + String.valueOf(userAdapter.getItem(i).getHighestScore()));
                tvSurvivalHighestScore.setText("Survival Highest Score: " + String.valueOf(userAdapter.getItem(i).getSurvivalHighScore()));
                LinearLayout linearLayout = view.findViewById(R.id.animationLayout);
                AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
                animationDrawable.start();
            }
        });
    }

}