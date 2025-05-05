package in.recordingclub.audio_books.adapters;

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

public class ChaptersAdapter extends ArrayAdapter<String> {

    private final String book;
    private final String[] title;
    private final String[] file_link;
    private final Activity activity;

    public ChaptersAdapter(Activity activity, String[] title, String[] file_link, String book) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, title);
        this.activity = activity;
        this.title = title;
        this.file_link = file_link;
        this.book = book;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        textView.setText(title[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RC_Player.class);
                intent.putExtra("file_title", title);
                intent.putExtra("book", book);
                intent.putExtra("file", file_link);
                intent.putExtra("current_position", position);
                intent.putExtra("isBook", true);
                activity.startActivity(intent);
            }
        });
        return convertView;
    }


}
