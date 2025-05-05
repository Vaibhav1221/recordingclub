package in.recordingclub.core.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import in.recordingclub.MainActivity;
import in.recordingclub.R;
import in.recordingclub.my_player.RC_Player;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private Map<String, String> data;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        data = remoteMessage.getData();
        Intent intent = new Intent(this, RC_Player.class);
        String url = data.get("url");
        String name = data.get("name");
        if (url == null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            String[] urlArray = new String[1];
            String[] nameArray = new String[1];
            urlArray[0] = url;
            nameArray[0] = name;
            intent.putExtra("file", urlArray);
            intent.putExtra("file_title", nameArray);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channel_id = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setContentIntent(pendingIntent)
                .setSound(defaultNotificationSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, builder.build());
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

}
