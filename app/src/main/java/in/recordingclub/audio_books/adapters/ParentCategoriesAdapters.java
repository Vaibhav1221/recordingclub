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

import in.recordingclub.audio_books.activities.AudioBooksSubCategoriesActivity;


public class ParentCategoriesAdapters extends ArrayAdapter<String> {

    private final String[] categories;
    private final Activity activity;

    public ParentCategoriesAdapters(Activity activity, String[] categories) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, categories);
        this.activity = activity;
        this.categories = categories;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        textView.setText(categories[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AudioBooksSubCategoriesActivity.class);
                intent.putExtra("parent_category_name", categories[position]);
                activity.startActivity(intent);
            }
        });
        return convertView;
    }
}
