package com.example.arcadeblastproject;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private int highestScore;
    private String profilePic;
    private int survivalHighScore;

    public User(String firstName, String lastName, String username, String password, int highestScore, int survivalHighScore) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.highestScore = highestScore;
        this.profilePic = String.valueOf(R.drawable.default_profile_pic);
        this.survivalHighScore = survivalHighScore;
    }

    public User(){

    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Username: " + username +
                "Highest Score: " + highestScore;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setSurvivalHighestScore(int score) {
        this.survivalHighScore = score;
    }

    public int getSurvivalHighScore() {
        return this.survivalHighScore;
    }
}
