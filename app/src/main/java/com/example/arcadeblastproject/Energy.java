package com.example.arcadeblastproject;

import android.content.SharedPreferences;

public class Energy {

    private static final int MAX_ENERGY = 30;
    private static SharedPreferences sharedPreferences;
    private static int currentEnergy;
    private static SharedPreferences.Editor editor;

    public Energy(){
        currentEnergy = 30;
    }

    public static boolean isNotFull(){
        if (currentEnergy < MAX_ENERGY){
            return true;
        }
        return false;
    }

    public static int getCurrentEnergy() {
        return currentEnergy;
    }

    public static void setCurrentEnergy(int currentEnergy) {
        Energy.currentEnergy = currentEnergy;
    }

    public static int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public static void setSharedPreferences(SharedPreferences sp){
        sharedPreferences = sp;
        editor = sharedPreferences.edit();
        currentEnergy = sharedPreferences.getInt("energyCount", 0);
    }

    public static boolean isFull() {
        return currentEnergy == MAX_ENERGY;
    }
}
