package com.example.arcadeblastproject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SnakeMainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnakeMainMenuFragment extends Fragment {

    private Intent startGame;
    private Intent goToLeaderboard;
    private static final String highScoreText = "Highest Score: ";
    private static int highScore;
    private static TextView highScoreTV;
    private static FirebaseFirestore db;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SnakeMainMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SnakeMainMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SnakeMainMenuFragment newInstance(String param1, String param2) {
        SnakeMainMenuFragment fragment = new SnakeMainMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startGame = new Intent(getActivity(), SnakeGameScreen.class);
        goToLeaderboard = new Intent(getActivity(), Leaderboard.class);
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(Login.getActiveUserName()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                try {
                    if (value != null) {
                        highScore = Objects.requireNonNull(value.get("highestScore", Integer.class));
                        setNewHighScore(highScore);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate( R.layout.fragment_snake_main_menu, container, false);
        return inflater.inflate(R.layout.fragment_snake_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        highScoreTV = getView().findViewById(R.id.highestScore);
        Button startGame = getView().findViewById(R.id.startButton);
        Button leaderboard = getView().findViewById(R.id.leaderboardButton);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SnakeGameScreen.class);
                startActivity(intent);
            }
        });

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Leaderboard.class);
                startActivity(intent);
            }
        });
    }

    public static void setNewHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            db.collection("users").document(Login.getActiveUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        User u = documentSnapshot.toObject(User.class);
                        if (u != null) {
                            u.setHighestScore(highScore);
                            db.collection("users").document(Login.getActiveUserName()).set(u);
                        }
                    }
                }
            });
        }

        highScoreTV.setText(highScoreText + highScore);
    }
}