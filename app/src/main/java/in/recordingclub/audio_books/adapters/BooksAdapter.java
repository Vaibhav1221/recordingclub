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
import com.videvelopers.app.vuh.AppInitializer;

import in.recordingclub.audio_books.activities.AudioBooksChaptersActivity;

public class BooksAdapter extends ArrayAdapter<String> {

    private final String[] books;
    private final Activity activity;
    private final AppInitializer initializer;
    private final View.OnLongClickListener onLongClickListener;

    public BooksAdapter(Activity activity, String[] books, AppInitializer initializer, View.OnLongClickListener onLongClickListener) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, books);
        this.activity = activity;
        this.books = books;
        this.initializer = initializer;
        this.onLongClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);

        // Set the text for the current book
        textView.setText(books[position]);

        // Set the click listeners
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Pass the position to the long click listener
                v.setTag(position);
                return onLongClickListener.onLongClick(v);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AudioBooksChaptersActivity.class);
                intent.putExtra("book_title", books[position]);
                activity.startActivity(intent);
            }
        });

        return convertView;
    }
}
