package com.example.arcadeblastproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> objects;
    private String from;

    public UserAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<User> objects, String from) {
        super(context, resource, textViewResourceId, objects);

        this.context = context;
        this.objects = objects;
        this.from = from;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.custom_listview, parent,false);
        LinearLayout linearLayout = view.findViewById(R.id.animationLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.start();

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvHighestScore = view.findViewById(R.id.tvHighScore);

        User temp = objects.get(position);

        tvUsername.setText(temp.getUsername() + "        ");
        if (from.equals("snake")) {
            tvHighestScore.setText(temp.getHighestScore() + "      ");
        } else if (from.equals("survival")){
            tvHighestScore.setText(temp.getSurvivalHighScore() + "      ");
        }
        return view;
    }

    @Override
    public int getCount() {
        return objects.size();
    }
}
