package in.recordingclub.whatsapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.databinding.WhatsappSetRequestActivityBinding;

public class WhatsAppSetRequestActivity extends AppCompatActivity implements View.OnClickListener {

    private WhatsappSetRequestActivityBinding binding;
    private AppInitializer initializer;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), WhatsAppSetRequestActivity.this);
        binding = WhatsappSetRequestActivityBinding.inflate(getLayoutInflater());
        setTitle("WhatsApp Request");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Others. Back");
        binding.wsrTlFullName.setHint(R.string.rmm_full_name);
        binding.wsrTlPhoneNumber.setHint(com.videvelopers.app.resources.R.string.profile_phone_number);
        binding.wsrBtnNext.setOnClickListener(this::onClick);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void setRequest(String fn_full_name, String fn_phone_number) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Requesting", "Posting request ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().whatsapp_set_request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        binding.wsrEtFullName.setText("");
                        binding.wsrEtPhoneNumber.setText("");
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        initializer.getAppFunctions().setAlertDialogForErrorFromServer(msg);
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    //initializer.getAppFunctions().setAlertDialogForServerError();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                initializer.getAppFunctions().setAlertDialogForServerError();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("full_name", fn_full_name);
                params.put("email_address", firebaseUser.getEmail());
                params.put("phone_number", fn_phone_number);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.wsrBtnNext.getId()) {
            String tmp1 = binding.wsrEtFullName.getText().toString();
            String tmp2 = binding.wsrEtPhoneNumber.getText().toString();
            if (tmp1.isEmpty() || tmp2.isEmpty()) {
                initializer.getAppFunctions().setAlertDialogForNoInformations();
            } else {
                setRequest(tmp1, tmp2);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
