package in.recordingclub.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SubscribersAdaptor extends ArrayAdapter<String> {

    private final Context context;
    private final AppCompatActivity activity;
    private final ArrayList<String> emails, phones, subscribed_date, renew_date, d, m, y;

    public SubscribersAdaptor(Context context, AppCompatActivity activity, ArrayList<String> emails, ArrayList<String> phones, ArrayList<String> subscribed_date, ArrayList<String> renew_date, ArrayList<String> d, ArrayList<String> m, ArrayList<String> y) {
        super(context, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, emails);
        this.context = context;
        this.activity = activity;
        this.emails = emails;
        this.phones = phones;
        this.subscribed_date = subscribed_date;
        this.renew_date = renew_date;
        this.d = d;
        this.m = m;
        this.y = y;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        TextView tv = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        tv.setText(emails.get(position));
        tv.setClickable(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SubscriberDetail.class);
                intent.putExtra("email", emails.get(position));
                intent.putExtra("phone", phones.get(position));
                intent.putExtra("sd", subscribed_date.get(position));
                intent.putExtra("rd", renew_date.get(position));
                intent.putExtra("d", d.get(position));
                intent.putExtra("m", m.get(position));
                intent.putExtra("y", y.get(position));
                activity.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return emails.size();
    }
}
