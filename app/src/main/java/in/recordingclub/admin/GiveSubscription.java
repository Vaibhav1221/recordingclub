package in.recordingclub.admin;

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
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.databinding.GiveSubscriptionBinding;


public class GiveSubscription extends AppCompatActivity {

    private GiveSubscriptionBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GiveSubscriptionBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), GiveSubscription.this);
        setContentView(binding.getRoot());
        setTitle("Give Subscription");
        binding.giveSubscriptionTil1.setHint("Email:");
        binding.giveSubscriptionTil2.setHint("Phone:");
        binding.giveSubscriptionGiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.giveSubscriptionEmail.getText().toString();
                String phone = binding.giveSubscriptionPhone.getText().toString();
                if (email.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(GiveSubscription.this, "Fill both field.", Toast.LENGTH_SHORT).show();
                } else {
                    setSubscription(email, phone);
                }
            }
        });
    }

    private void setSubscription(String fn_email, String fn_phone) {
        initializer.getAppFunctions().setShowProgressDialogWithTitle("Please Wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().setSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(GiveSubscription.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(GiveSubscription.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(GiveSubscription.this, "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(GiveSubscription.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", fn_email);
                params.put("phone", fn_phone);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }


}