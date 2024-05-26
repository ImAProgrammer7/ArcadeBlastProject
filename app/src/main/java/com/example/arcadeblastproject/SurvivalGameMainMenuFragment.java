package com.example.arcadeblastproject;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SurvivalGameMainMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurvivalGameMainMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button startGame;
    private Button instructions;
    private TextView survivalHighestScoreTV;
    private Intent intent;
    private SharedPreferences sharedPreferences;
    private TextView energyTextView;
    private Button survivalLeaderboardButton;

    public SurvivalGameMainMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SurvivalGameMainMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SurvivalGameMainMenuFragment newInstance(String param1, String param2) {
        SurvivalGameMainMenuFragment fragment = new SurvivalGameMainMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survival_game_main_menu, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        int currentEnergy = sharedPreferences.getInt("energyCount", 0);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(String.valueOf(currentEnergy));
        ssb.append("/");
        ssb.append(String.valueOf(Energy.getMaxEnergy()));
        ssb.setSpan(new ImageSpan(requireContext(), R.drawable.baseline_bolt_24),
                ssb.length(),
                ssb.length(),
                0);
        energyTextView.setText(ssb);
        if (!isMyServiceRunning(SurvivalServices.class)){
            getContext().startService(intent);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startGame = getView().findViewById(R.id.survivalStartButton);
        instructions = getView().findViewById(R.id.survivalGameRules);
        survivalHighestScoreTV = getView().findViewById(R.id.survivalHighestScore);
        energyTextView = getView().findViewById(R.id.energyTextView);
        survivalLeaderboardButton = getView().findViewById(R.id.survivalLeaderboardButton);
        intent = new Intent(getContext(), SurvivalServices.class);
        sharedPreferences = getContext().getSharedPreferences("energy", 0);
        Energy.setSharedPreferences(sharedPreferences);

        int currentEnergy = sharedPreferences.getInt("energyCount", 0);
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(String.valueOf(currentEnergy));
        ssb.append("/");
        ssb.append(String.valueOf(Energy.getMaxEnergy()));
        ssb.setSpan(new ImageSpan(getContext(), R.drawable.baseline_bolt_24),
                ssb.length(),
                ssb.length(),
                0);
        energyTextView.setText(ssb);

        if (!isMyServiceRunning(SurvivalServices.class)) {
            getContext().startService(intent);
        }

        if (Energy.isNotFull() && !isMyServiceRunning(SurvivalServices.class)) {
            getContext().startService(intent);
        }

        final int[] highestScore = {0};
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").document(Login.getActiveUserName()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User u = documentSnapshot.toObject(User.class);
                    if (u != null) {
                        highestScore[0] = u.getSurvivalHighScore();
                        survivalHighestScoreTV.setText("Highest Score: " + highestScore[0]);
                    }
                }
            }
        });

        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());

                dialog.setContentView(R.layout.survival_instructions);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.create();

                dialog.show();
            }
        });

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go = new Intent(getContext(), SurvivalGameScreen.class);
                if (Energy.getCurrentEnergy() - 5 >= 0) {
                    Energy.setCurrentEnergy(Energy.getCurrentEnergy() - 5);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("energyCount", Energy.getCurrentEnergy());
                    editor.apply();

                    startActivity(go);
                }
            }
        });

        survivalLeaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent leaderboard = new Intent(getContext(), SurvivalLeaderboard.class);
                startActivity(leaderboard);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}