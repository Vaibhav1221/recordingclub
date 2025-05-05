package in.recordingclub.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class DMAdaptor extends ArrayAdapter<String> {

    private final Context context;
    private final AppCompatActivity activity;
    private final ArrayList<String> names;
    private final ArrayList<String> emails;

    public DMAdaptor(Context context, AppCompatActivity activity, ArrayList<String> names, ArrayList<String> emails) {
        super(context, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, names); // Pass 'names' array list as data source
        this.context = context;
        this.activity = activity;
        this.names = names;
        this.emails = emails;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        TextView tv = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        tv.setText(names.get(position) + "\n" + emails.get(position));

        return convertView;
    }

    @Override
    public int getCount() {
        return names.size(); // Return the size of the data list
    }
}
