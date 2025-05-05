package in.recordingclub.admin;

import android.content.DialogInterface;
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

import in.recordingclub.databinding.SubscriberDetailActivityBinding;

public class SubscriberDetail extends AppCompatActivity {

    AppInitializer initializer;
    String email, phone, d, m, y;
    private SubscriberDetailActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubscriberDetailActivityBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), SubscriberDetail.this);
        setTitle("Subscriber detail");
        setContentView(binding.getRoot());
        try {
            Intent intent = getIntent();
            email = intent.getStringExtra("email");
            phone = intent.getStringExtra("phone");
            String renew_date = intent.getStringExtra("rd");
            d = intent.getStringExtra("d");
            m = intent.getStringExtra("m");
            y = intent.getStringExtra("y");
            String subscribed_date = intent.getStringExtra("sd");
            binding.tv1.setText("Email\n" + email);
            binding.tv2.setText("Phone\n" + phone);
            binding.tv3.setText("Subscribed date\n" + subscribed_date);
            binding.tv4.setText("Renew date\n" + renew_date);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        binding.editSubscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubscriberDetail.this, EditSubscription.class);
                i.putExtra("email", email);
                i.putExtra("phone", phone);
                i.putExtra("d", d);
                i.putExtra("m", m);
                i.putExtra("y", y);
                startActivity(i);
            }
        });

        binding.deleteSubscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initializer.getAppFunctions().setDisplayAlertDialog("Delete subscription?", "Are you sure you want to delete this subscription?", "Cancel", "Delete subscription", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteSubscription(email);
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(SubscriberDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteSubscription(String fn_email) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please wait...", "Deleting");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().deleteSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    initializer.getAppFunctions().setHideProgressDialog();
                    if (!error) {
                        Toast.makeText(SubscriberDetail.this, msg, Toast.LENGTH_SHORT).show();
                        SubscriberDetail.this.finish();
                    } else {
                        Toast.makeText(SubscriberDetail.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(SubscriberDetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(SubscriberDetail.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", fn_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }


}