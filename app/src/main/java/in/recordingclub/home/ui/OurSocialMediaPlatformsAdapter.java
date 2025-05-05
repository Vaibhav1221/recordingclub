package in.recordingclub.home.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import in.recordingclub.whatsapp.WhatsAppSetRequestActivity;

public class OurSocialMediaPlatformsAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] platforms;

    public OurSocialMediaPlatformsAdapter(Activity activity, String[] platforms) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, platforms);
        this.activity = activity;
        this.platforms = platforms;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        textView.setText(platforms[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position) {
                    case 0:
                        activity.startActivity(new Intent(activity, WhatsAppSetRequestActivity.class));
                        break;
                    case 1:
                        Intent i4 = new Intent(Intent.ACTION_VIEW);
                        i4.setData(Uri.parse("https://chat.whatsapp.com/FrwDTv4cJFZDPGFEzJFwSh"));
                        activity.startActivity(i4);
                        break;
                    case 2:
                        Intent i2 = new Intent(Intent.ACTION_VIEW);
                        i2.setData(Uri.parse("https://www.youtube.com/channel/UClSjvN0j8rHOiLrequly03g"));
                        activity.startActivity(i2);
                        break;
                    case 3:
                        Intent i3 = new Intent(Intent.ACTION_VIEW);
                        i3.setData(Uri.parse("https://www.facebook.com/RecordingClubAjmer/"));
                        activity.startActivity(i3);
                        break;
                }
            }
        });
        return convertView;
    }
}
