package in.recordingclub.audio_books;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.databinding.SendFeedbackBinding;

public class SendFeedback extends AppCompatActivity {

    private AppInitializer initializer;
    private SendFeedbackBinding binding;
    private String name, email, book, feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SendFeedbackBinding.inflate(getLayoutInflater());
        setTitle("Feedback");
        setContentView(binding.getRoot());
        initializer = new AppInitializer(getApplicationContext(), SendFeedback.this);
        binding.fdEdtL.setHint("Your Feedback :");
        try {
            Intent intent = getIntent();
            book = intent.getStringExtra("book_name");
        } catch (Exception e) {

        }
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            name = account.getGivenName();
            email = account.getEmail();
        }
        binding.fdSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback = binding.fdEdtI.getText().toString();
                sendFeedback(name, email, book, feedback);
            }
        });
    }

    private void sendFeedback(String fn_name, String fn_email, String fn_book, String fn_feedback) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Sending Feedback.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().sendFeedback, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("msg");
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(SendFeedback.this, msg, Toast.LENGTH_SHORT).show();
                        SendFeedback.this.finish();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(SendFeedback.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(SendFeedback.this, "Error in response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(SendFeedback.this, "Error in networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", fn_name);
                params.put("email", fn_email);
                params.put("book", fn_book);
                params.put("feedback", fn_feedback);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }
}