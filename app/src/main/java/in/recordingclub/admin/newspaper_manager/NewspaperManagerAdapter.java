package in.recordingclub.admin.newspaper_manager;

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

public class NewspaperManagerAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] features_list;

    public NewspaperManagerAdapter(Activity activity, String[] features_list) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_button, features_list);
        this.activity = activity;
        this.features_list = features_list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_button, null);
        MaterialButton button = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_btn);
        button.setText(features_list[position]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feature = features_list[position];
                if (feature.equals("Create newspaper")) {
                    activity.startActivity(new Intent(activity, CreateNewspaperActivity.class));
                } else if (feature.equals("Create newspaper daily post")) {
                    activity.startActivity(new Intent(activity, CreateNewspaperDailyPostActivity.class));
                } else if (feature.equals("Delete Newspaper")) {
                    activity.startActivity(new Intent(getContext(), DeleteNewspaper.class));
                }
            }
        });
        return convertView;
    }
}
