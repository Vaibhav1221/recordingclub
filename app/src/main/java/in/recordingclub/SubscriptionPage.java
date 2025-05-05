package in.recordingclub;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.databinding.SubscriptionPageBinding;
import in.recordingclub.home.HomeActivity;

public class SubscriptionPage extends AppCompatActivity implements PaymentResultListener {

    private final String sp_item = "";
    private SubscriptionPageBinding binding;
    private String order_id, amount, cs_name, cs_email, cs_phone;
    private int paymentAmount;
    private String subscriptionAmount = "";
    private String amount_in_str = "";
    private AppInitializer initializer;
    private String prise_msg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SubscriptionPageBinding.inflate(getLayoutInflater());
        setTitle("Subscribe Recording Club!");
        setContentView(binding.getRoot());
        initializer = new AppInitializer(getApplicationContext(), SubscriptionPage.this);
        getAmount();
        binding.numberTl.setHint("Phone number:");
        binding.prize.setText(prise_msg);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            cs_name = account.getDisplayName();
            cs_email = account.getEmail();
        }
        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cs_phone = binding.numberTt.getText().toString();
                if (cs_phone.isEmpty() || amount_in_str.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    subscribe();
                }
            }
        });
    }

    private void getAmount() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Whait...", "Openning Recording Club");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().getAmount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    subscriptionAmount = jsonObject.getString("amount");
                    amount_in_str = subscriptionAmount;
                    prise_msg = jsonObject.getString("amount_msg");

                    binding.prize.setText(prise_msg);
                    initializer.getAppFunctions().setHideProgressDialog();
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(SubscriptionPage.this, "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(SubscriptionPage.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void subscribe() {
        int paymentAmount = Math.round(Float.parseFloat(amount_in_str) * 100);
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_0E6Jki0iEfzK4S");
        JSONObject options = new JSONObject();
        try {
            options.put("name", "Recording Club");
            options.put("currency", "INR");
            options.put("amount", paymentAmount);
            options.put("prefill.email", cs_email);
            options.put("prefill.contact", cs_phone);
            checkout.open(SubscriptionPage.this, options);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error while processing the request.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        setSubscription(cs_email, cs_phone);
    }

    @Override
    public void onPaymentError(int i, String s) {
        initializer.getAppFunctions().setDisplayAlertDialog("Payment failed", "Your payment for the Recording Club subscription has failed. If your payment has been processed, please contact the Recording Club team at recordingclub2016@gmail.com for assistance.", "Cancel", "Connect with team", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SubscriptionPage.this.finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:01453566471"));
                startActivity(intent);
            }
        });
    }

    private void setSubscription(String fn_email, String fn_phone) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Processing Payment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().setSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("msg");
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        SubscriptionPage.this.finish();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Error while getting subscription response!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(SubscriptionPage.this, "Error in networking, contact to RC team.", Toast.LENGTH_SHORT).show();
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

    private void setMonthlySubscription(String fn_email, String fn_phone) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Processing Payment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().setMonthlySubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("msg");
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        SubscriptionPage.this.finish();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Error while getting subscription response!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(SubscriptionPage.this, "Error in networking, contact to RC team.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        this.finish();
    }
}