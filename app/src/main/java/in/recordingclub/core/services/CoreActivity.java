package in.recordingclub.core.services;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class CoreActivity extends AppCompatActivity {

    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), CoreActivity.this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        if (isActiveSession(user_id)) {
            Toast.makeText(this, "User session active on another device.", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            this.finish();
        } else {
            startHeartbeatService();
        }
    }

    private boolean isActiveSession(String user_id) {
        String url = "https://api.recordingclub.in/isActiveSession.php";
        boolean returnValue = false;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isActive = jsonObject.getBoolean("isActive");
                } catch (Exception e) {
                    Toast.makeText(CoreActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CoreActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(request);
        return returnValue;
    }

    private void startHeartbeatService() {
        Intent serviceIntent = new Intent(this, HeartbeatService.class);
        startService(serviceIntent);
    }

    private void stopHeartbeatService() {
        Intent serviceIntent = new Intent(this, HeartbeatService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHeartbeatService();
    }
}