package in.recordingclub.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import in.recordingclub.MainActivity;
import in.recordingclub.R;
import in.recordingclub.SubscriptionPage;
import in.recordingclub.databinding.HomeActivityBinding;

public class HomeActivity extends AppCompatActivity implements PaymentResultListener {

    private final String sp_item = "";
    private AppInitializer initializer;
    private AppBarConfiguration mAppBarConfiguration;
    private HomeActivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String user_email;
    private int user_phone;
    private String subscriptionAmount = "";
    private String amount_in_str = "";
    private boolean r = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeActivityBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), HomeActivity.this);
        getAmount();

        setContentView(binding.getRoot());
        setTitle("Recording Club");
        setSupportActionBar(binding.appBarHome.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        File file = new File(getCacheDir(), "tmp_paths");
        if (!file.exists()) {
            file.mkdir();
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        user_email = firebaseUser.getEmail();

        Toast.makeText(this, "Welcome " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home_nav_home, R.id.library_nav_menu, R.id.home_nav_account, R.id.home_nav_osmp, R.id.home_nav_contact_us, R.id.home_nav_about_us, R.id.home_nav_pp, R.id.share_f)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        getAppUpdate();
        getSubscriptionStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setUser(String token) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.recordingclub.in/fcm/setup_notification.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Process response if needed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error if needed
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", firebaseUser.getDisplayName());
                params.put("user_email_address", firebaseUser.getEmail());
                params.put("user_token", token);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getAmount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().getAmount, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    subscriptionAmount = jsonObject.getString("amount");
                    amount_in_str = subscriptionAmount;
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Error while getting subscription amount.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getSubscriptionStatus() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Opening Recording Club.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().getSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    initializer.getAppFunctions().setHideProgressDialog();
                    if (!error) {
                        boolean status = jsonObject.getBoolean("status");
                        int user_role = jsonObject.getInt("user_role");
                        int expire = jsonObject.getInt("expire");
                        if (user_role == 1 || user_role == 2) {
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful()) {
                                                setUser(task.getResult());
                                            }
                                        }
                                    });
                        }
                        if (user_role == 0) {
                            if (status) {
                                if (expire == 0) {
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                @Override
                                                public void onComplete(@NonNull Task<String> task) {
                                                    if (task.isSuccessful()) {
                                                        setUser(task.getResult());
                                                    }
                                                }
                                            });
                                }
                                if (expire == 1) {
                                    user_phone = jsonObject.optInt("phone", 0); // Use optInt() with default value to handle empty strings
                                    renewSubscription();
                                }
                            }
                        }
                        if (user_role == 0) {
                            if (!status) {
                                startActivity(new Intent(getApplicationContext(), SubscriptionPage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                HomeActivity.this.finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error while getting subscription status.", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(HomeActivity.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", user_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void renewSubscription() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(HomeActivity.this);
        builder.setTitle("Renew Subscription");
        builder.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.renew_subscription_layout, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        MaterialButton cancel = view.findViewById(R.id.renew_btn_cancel);
        MaterialButton renew = view.findViewById(R.id.renew_btn_renew);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.this.finish();
            }
        });

        renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amount_in_str;
                if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(getApplicationContext(), "Amount is not available.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int paymentAmount = Math.round(Float.parseFloat(amount) * 100);
                Checkout checkout = new Checkout();
                checkout.setKeyID("rzp_live_0E6Jki0iEfzK4S");
                JSONObject options = new JSONObject();
                try {
                    options.put("name", "Recording Club");
                    options.put("currency", "INR");
                    options.put("amount", paymentAmount);
                    options.put("prefill.email", user_email);

                    // Check if the user_phone is not empty before parsing it to an integer
                    if (user_phone != 0) {
                        options.put("prefill.contact", user_phone);
                    }

                    checkout.open(HomeActivity.this, options);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error while processing the request.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPaymentSuccess(String s) {
        updateSubscription(user_email);
    }

    @Override
    public void onPaymentError(int i, String s) {
        initializer.getAppFunctions().setDisplayAlertDialog("Payment failed", "Your payment for the Recording Club subscription has failed. If your payment has been processed, please contact the Recording Club team at recordingclub2016@gmail.com for assistance.", "Cancel", "Connect with team", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HomeActivity.this.finish();
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

    private void updateSubscription(String fn_email) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Processing Payment.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().updateSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(HomeActivity.this, "Error In Response, Contact RC Team To Resolve This Issue.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(HomeActivity.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", fn_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void updateMonthlySubscription(String fn_email) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Processing Payment.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().updateMonthlySubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(HomeActivity.this, "Error In Response, Contact RC Team To Resolve This Issue.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(HomeActivity.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", fn_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getAppUpdate() {
        int appVersionCode = initializer.getAppFunctions().getAppVersionCode();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().getAppUpdate, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean updated = jsonObject.getBoolean("updated");
                    String msg = jsonObject.getString("msg");
                    if (!updated) {
                        initializer.getAppFunctions().setDisplayAlertDialog("Update Available", msg, "Cancel", "Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HomeActivity.this.finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=in.recordingclub"));
                                startActivity(intent);
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("v_code", String.valueOf(appVersionCode));
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getSessionStatus(String u_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.recordingclub.in/check_session.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        r = jsonObject.getBoolean("isLoggedIn");
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", u_id);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

}
