package in.recordingclub.admin.audio_books_manager;

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


public class AudioBooksManagerAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] features;

    public AudioBooksManagerAdapter(Activity activity, String[] features) {
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
                String feature = features[position];
                if (feature.equals("Create root category")) {
                    activity.startActivity(new Intent(activity, CreateRootActivity.class));
                } else if (feature.equals("Create sub category")) {
                    activity.startActivity(new Intent(activity, CreateSubCategoryActivity.class));
                } else if (feature.equals("Create audio book")) {
                    activity.startActivity(new Intent(activity, CreateBooksActivity.class));
                } else if (feature.equals("Create chapter")) {
                    activity.startActivity(new Intent(activity, CreateChapterActivity.class));
                } else if (feature.equals("Delete Book")) {
                    activity.startActivity(new Intent(getContext(), DeleteAudioBook.class));
                } else if (feature.equals("Delete Chapter")) {
                    activity.startActivity(new Intent(getContext(), DeleteChapter.class));
                }
            }
        });
        return convertView;
    }
}
