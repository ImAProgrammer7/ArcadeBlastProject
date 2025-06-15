package com.example.arcadeblastproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Lobby extends AppCompatActivity {

    private TextView playerName;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static int roomNumber = 0;
    public static ArrayList<DatabaseReference> players;
    private DatabaseReference myRef = database.getReference("Player 1: ");
    private DatabaseReference myRef2 = database.getReference("Player 2: ");
    private DatabaseReference playerCountRef = database.getReference("Player Count: ");
    public static GameStatus gameStatus;
    public final int MAX_PLAYERS = 2;
    public int currentPlayers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        players = new ArrayList<>();

        //playerCountRef.setValue(currentPlayers);
        Log.d("nigingignig", String.valueOf(currentPlayers));

        playerCountRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                currentPlayers = dataSnapshot.getValue(Integer.class);
                System.out.println(currentPlayers);
            }
        }).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                currentPlayers++;
                Log.d("nigingignig", String.valueOf(currentPlayers));

                playerName = findViewById(R.id.playerName);
                playerName.setText(Login.getActiveUserName() + " Waiting...");
                gameStatus = GameStatus.CREATED;

                if (currentPlayers == 1) {
                    myRef.setValue(Login.getActiveUserName());
                    playerCountRef.setValue(currentPlayers);
                }
                if (currentPlayers == MAX_PLAYERS) {
                    myRef2.setValue(Login.getActiveUserName());
                    playerCountRef.setValue(currentPlayers);
                    players.add(myRef);
                    players.add(myRef2);
                    System.out.println(players.size());
                }
                playerCountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue(Integer.class) == MAX_PLAYERS){
                            playerCountRef.setValue(currentPlayers);
                            myRef.setValue(Login.getActiveUserName());
                            roomNumber++;
                            goToGame();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });




    }

    private void goToGame() {
        currentPlayers = 0;
        playerCountRef.setValue(currentPlayers);
        gameStatus = GameStatus.IN_PROGRESS;
        Intent go = new Intent(this, MultiplayerGameScreen.class);
        startActivity(go);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        myRef.setValue(currentPlayers--);
    }

    public enum GameStatus {
        CREATED,
        IN_PROGRESS,
        FINISHED
    }
}