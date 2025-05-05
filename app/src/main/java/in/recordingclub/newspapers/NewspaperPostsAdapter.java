package in.recordingclub.newspapers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.videvelopers.app.vuh.AppInitializer;

import in.recordingclub.R;
import in.recordingclub.SubscriptionPage;
import in.recordingclub.my_player.RC_Player;

public class NewspaperPostsAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final String[] newspaper_title;
    private final String[] newspaper_file_link;
    private final AppInitializer initializer;
    private boolean subscriber = false;

    public NewspaperPostsAdapter(Activity activity, String[] newspaper_title, String[] newspaper_file_link, AppInitializer initializer, boolean subscriber) {
        super(activity, com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, newspaper_title);
        this.activity = activity;
        this.newspaper_title = newspaper_title;
        this.newspaper_file_link = newspaper_file_link;
        this.initializer = initializer;
        this.subscriber = subscriber;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(com.videvelopers.app.resources.R.layout.custom_list_view_with_text_view, null);
        MaterialTextView textView = convertView.findViewById(com.videvelopers.app.resources.R.id.custom_list_view_tv);
        textView.setText(newspaper_title[position]);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String user_email = firebaseUser.getEmail();
                String user_name = firebaseUser.getDisplayName();
                int ac = 0;
                String sub_msg = "Hello " + firebaseUser.getDisplayName() + "!\n" + activity.getString(R.string.sub_buy_msg);
                if (subscriber) {
                    initializer.getAppFunctions().setDisplayAlertDialog("Subscribe Recording Club", sub_msg, "Cancel", "Subscribe", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            NewspaperPostsAdapter.this.notifyDataSetChanged();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            activity.startActivity(new Intent(getContext(), SubscriptionPage.class));
                        }
                    });
                }
                if (!subscriber) {
                    Intent intent = new Intent(activity, RC_Player.class);
                    intent.putExtra("file", newspaper_file_link);
                    intent.putExtra("file_title", newspaper_title);
                    intent.putExtra("current_position", position);
                    intent.putExtra("isBook", false);
                    activity.startActivity(intent);
                }
            }
        });
        return convertView;
    }
}
