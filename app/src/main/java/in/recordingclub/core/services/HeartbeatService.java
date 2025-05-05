package in.recordingclub.core.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.videvelopers.app.vuh.app_components.AppCore;

import java.util.HashMap;
import java.util.Map;


public class HeartbeatService extends Service {

    private static final String TAG = "HeartbeatService";
    private static final long HEARTBEAT_INTERVAL = 60 * 1000; // 5 minutes


    private final Handler handler = new Handler();
    private final Runnable heartbeatRunnable = new Runnable() {
        @Override
        public void run() {
            sendHeartbeatToServer();
            handler.postDelayed(this, HEARTBEAT_INTERVAL); // Schedule next heartbeat
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startHeartbeat();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopHeartbeat();
        super.onDestroy();
    }

    private void startHeartbeat() {
        handler.postDelayed(heartbeatRunnable, HEARTBEAT_INTERVAL);
        Log.d(TAG, "Heartbeat service started");
        //Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        sendHeartbeatToServer();
    }

    private void stopHeartbeat() {
        handler.removeCallbacks(heartbeatRunnable);
        Log.d(TAG, "Heartbeat service stopped");
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    private void sendHeartbeatToServer() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getEmail();
        // Implement logic to send heartbeat to server (e.g., make an HTTP request)
        // Here, replace "YOUR_SERVER_URL" with your actual server URL
        String url = "https://api.recordingclub.in/heartbeat.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Heartbeat sent to server");
                        //Toast.makeText(HeartbeatService.this, response, Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error sending heartbeat to server: " + error.getMessage());
                        Toast.makeText(HeartbeatService.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(HeartbeatService.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // Add any parameters needed for the heartbeat request
                // For example, you might need to send user ID or device ID
                params.put("user_id", user_id);
                return params;
            }
        };

        // Add the request to the RequestQueue (assuming you have a singleton RequestQueue)
        AppCore.getInstance().addToRequestQueue(request);
    }
}
