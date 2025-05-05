package in.recordingclub.newspapers;

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

public class NewspapersAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] newspapers;

    public NewspapersAdapter(Activity activity, String[] newspapers) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, newspapers);
        this.activity = activity;
        this.newspapers = newspapers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        textView.setText(newspapers[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NewspaperPostsActivity.class);
                intent.putExtra("newspaper_name", newspapers[position]);
                activity.startActivity(intent);
            }
        });
        return convertView;
    }
}
