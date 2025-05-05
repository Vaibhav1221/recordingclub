package in.recordingclub.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;

public class SendNotification extends AppCompatActivity {

    private String title, body;
    private AppInitializer initializer;
    private Button send;
    private TextInputLayout textInputLayout, textInputLayout2;
    private TextInputEditText textInputEditText, textInputEditText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Send Notification");
        setContentView(R.layout.send_notification);
        initializer = new AppInitializer(getApplicationContext(), SendNotification.this);
        textInputEditText = findViewById(R.id.send_notification_et);
        textInputLayout = findViewById(R.id.et_send_et1);
        textInputEditText2 = findViewById(R.id.send_notification_et2);
        textInputLayout2 = findViewById(R.id.et_send_et2);
        send = findViewById(R.id.send_notification_btn);
        textInputLayout.setHint("Notification Title :");
        textInputLayout2.setHint("Notification Body :");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = textInputEditText.getText().toString();
                body = textInputEditText2.getText().toString();
                sendNotification(title, body);
            }
        });

    }

    private void sendNotification(String fn_title, String fn_body) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Sending Notification");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().send_notification_to_all, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        textInputEditText.setText("");
                        textInputEditText2.setText("");
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(SendNotification.this, "Server Response Error", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error while sending the notification.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", fn_title);
                params.put("body", fn_body);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
        int MY_SOCKET_TIMEOUT_MS = 19000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}