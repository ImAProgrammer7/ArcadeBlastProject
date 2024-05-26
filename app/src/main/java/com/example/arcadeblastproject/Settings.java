package com.example.arcadeblastproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment implements CompoundButton.OnCheckedChangeListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SwitchMaterial turnOffVibration;
    private SwitchMaterial turnOffMusic;
    private SwitchMaterial turnOffSoundEffects;
    private static SharedPreferences sharedPreferences;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        turnOffVibration = getView().findViewById(R.id.turnOffVibration);
        turnOffMusic = getView().findViewById(R.id.turnOffMusic);
        turnOffSoundEffects = getView().findViewById(R.id.turnOffSoundEffects);
        turnOffVibration.setOnCheckedChangeListener(this);
        turnOffMusic.setOnCheckedChangeListener(this);
        turnOffSoundEffects.setOnCheckedChangeListener(this);

        sharedPreferences = getActivity().getSharedPreferences("settings", 0);
        if (String.valueOf(sharedPreferences.getBoolean("vibration", false)) != null ||
                String.valueOf(sharedPreferences.getBoolean("music", false)) != null ||
                String.valueOf(sharedPreferences.getBoolean("soundEffects", false)) != null){
            turnOffVibration.setChecked(sharedPreferences.getBoolean("vibration", false));
            turnOffMusic.setChecked(sharedPreferences.getBoolean("music", false));
            turnOffSoundEffects.setChecked(sharedPreferences.getBoolean("soundEffects", false));

        }

        SnakeGameScreen.setSharedPreferences(sharedPreferences);
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.turnOffVibration){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("vibration", b);
            editor.apply();
        }
        if (compoundButton.getId() == R.id.turnOffMusic){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("music", b);
            editor.apply();
        }
        if (compoundButton.getId() == R.id.turnOffSoundEffects){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("soundEffects", b);
            editor.apply();
        }
    }

}