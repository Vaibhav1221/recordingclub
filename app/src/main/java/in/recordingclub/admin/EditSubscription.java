package in.recordingclub.admin;

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
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.databinding.ChangeSubscriptionBinding;


public class EditSubscription extends AppCompatActivity {

    ChangeSubscriptionBinding binding;
    AppInitializer initializer;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChangeSubscriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Edit Subscription");
        initializer = new AppInitializer(getApplicationContext(), EditSubscription.this);

        binding.editSubscriptionTil1.setHint("Phone:");
        binding.editSubscriptionTil2.setHint("Date:");
        binding.editSubscriptionTil3.setHint("Month");
        binding.editSubscriptionTil4.setHint("Year:");

        Intent data = getIntent();
        try {
            email = data.getStringExtra("email");
            String tmp1 = data.getStringExtra("phone");
            String tmp2 = data.getStringExtra("d");
            String tmp3 = data.getStringExtra("m");
            String tmp4 = data.getStringExtra("y");
            binding.editSubscriptionTie1.setText(tmp1);
            binding.editSubscriptionTie2.setText(tmp2);
            binding.editSubscriptionTie3.setText(tmp3);
            binding.editSubscriptionTie4.setText(tmp4);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        binding.editSubscriptionBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp1 = binding.editSubscriptionTie1.getText().toString();
                String tmp2 = binding.editSubscriptionTie2.getText().toString();
                String tmp3 = binding.editSubscriptionTie3.getText().toString();
                String tmp4 = binding.editSubscriptionTie4.getText().toString();
                setChangeSubscription(email, tmp1, tmp2, tmp3, tmp4);
            }
        });

    }

    private void setChangeSubscription(String email, String phone, String date, String month, String year) {
        initializer.getAppFunctions().setShowProgressDialogWithTitle("Please wait...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().editSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(EditSubscription.this, msg, Toast.LENGTH_SHORT).show();
                        EditSubscription.this.finish();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(EditSubscription.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(EditSubscription.this, "Error while getting response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(EditSubscription.this, "Error while networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("phone", phone);
                params.put("date", date);
                params.put("month", month);
                params.put("year", year);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

}