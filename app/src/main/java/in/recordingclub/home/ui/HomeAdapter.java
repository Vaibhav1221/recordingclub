package in.recordingclub.home.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import in.recordingclub.audio_books.AudioBooksActivity;
import in.recordingclub.newspapers.NewspaperActivity;
import in.recordingclub.rc_matrimonial.Profiles;

public class HomeAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] features;

    public HomeAdapter(Activity activity, String[] features) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_button, features);
        this.activity = activity;
        this.features = features;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_button, null);
        MaterialButton button = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_btn);
        button.setText(features[position]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feature_name = features[position];
                if (feature_name.equals("Newspapers")) {
                    activity.startActivity(new Intent(activity, NewspaperActivity.class));
                } else if (feature_name.equals("Audio Books")) {
                    activity.startActivity(new Intent(activity, AudioBooksActivity.class));
                } else if (feature_name.equals("Hamrahi.com")) {
                    activity.startActivity(new Intent(getContext(), Profiles.class));
                }
            }
        });
        return convertView;
    }
}
