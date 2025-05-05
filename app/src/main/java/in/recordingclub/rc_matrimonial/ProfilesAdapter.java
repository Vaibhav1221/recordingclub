package in.recordingclub.rc_matrimonial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import in.recordingclub.my_player.RC_Player;

public class ProfilesAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] profile_title;
    private final String[] profile_file_link;
    private final int mPosition = 0;
    private final View.OnLongClickListener onLongClickListener;

    public ProfilesAdapter(Activity activity, String[] profile_title, String[] profile_file_link, View.OnLongClickListener onLongClickListener) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, profile_title);
        this.activity = activity;
        this.profile_file_link = profile_file_link;
        this.profile_title = profile_title;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, parent, false);
        }

        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);


        // Set the title and handle click events for non-empty lists
        textView.setText(profile_title[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RC_Player.class);
                intent.putExtra("file", profile_file_link);
                intent.putExtra("file_title", profile_title);
                intent.putExtra("current_position", position);
                activity.startActivity(intent);
            }
        });
        textView.setOnLongClickListener(onLongClickListener);
        return convertView;
    }

    public int getPosition() {
        return mPosition;
    }
}
