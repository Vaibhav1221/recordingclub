package in.recordingclub;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.databinding.MainBinding;
import in.recordingclub.home.HomeActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private MainBinding binding;
    private AppInitializer initializer;
    private FirebaseAuth firebaseAuth;
    private String user_name = "", user_email_address = "";
    private boolean r = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED&&ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
        binding = MainBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), MainActivity.this);
        setTitle("Recording Club");
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(getApplicationContext());
        if (initializer.getAppFunctions().getSDKAPIVersion() >= 26) {
            CharSequence name = getString(in.recordingclub.R.string.default_notification_channel_name);
            String channel_id = getString(in.recordingclub.R.string.default_notification_channel_id);
            String description = getString(in.recordingclub.R.string.default_notification_channel_description);
            int importants = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, name, importants);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        if (initializer.getAppFunctions().isInternetConnected()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 121);
            }
            firebaseAuth = FirebaseAuth.getInstance();
            initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("573496280529-h4v8h8u1ea52jj2d4um8uaucb2i2euf0.apps.googleusercontent.com")
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                binding.mainBtnLogin.setOnClickListener(this::onClick);
            } else {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                MainActivity.this.finish();
            }
        } else {
            initializer.getAppFunctions().setCreateAlertDialogForNotConnectedToTheInternet();
        }
        //}
        //else {
        //ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},getResources().getInteger(R.integer.default_activity_request_code));
        //}
    }

    private void setSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, getResources().getInteger(com.videvelopers.app.resources.R.integer.default_activity_request_code));
    }

    private void setFirebaseAuth(String id) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(id, null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setLogin();
                        } else {

                        }
                    }
                });
    }

    private void setLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().account_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        String msg = jsonObject.getString("msg");
                        boolean isSubscriber = jsonObject.getBoolean("isSubscriber");
                        int user_role = jsonObject.getInt("user_role");

                        if (isSubscriber) {
                            initializer.getAppFunctions().setHideProgressDialog();
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            MainActivity.this.finish();
                        } else {
                            if (user_role == 0) {
                                initializer.getAppFunctions().setHideProgressDialog();
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), SubscriptionPage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                MainActivity.this.finish();
                            } else {
                                initializer.getAppFunctions().setHideProgressDialog();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                MainActivity.this.finish();
                            }
                        }
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(getApplicationContext(), getResources().getString(com.videvelopers.app.resources.R.string.default_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(com.videvelopers.app.resources.R.string.default_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", user_name);
                params.put("user_email_address", user_email_address);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private boolean isLoggedIn(String user_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.recordingclub.in/check_session.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean isUserLoggedIn = jsonObject.getBoolean("isUserLoggedIn");
                    r = isUserLoggedIn;
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
        AppCore.getInstance().addToRequestQueue(stringRequest);
        return r;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.mainBtnLogin.getId()) {
            setSignIn();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 121) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You will receive all notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(com.videvelopers.app.resources.R.integer.default_activity_request_code)) {
            initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Loging In.");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                user_name = account.getDisplayName();
                user_email_address = account.getEmail();
                setFirebaseAuth(account.getIdToken());
            } catch (Exception e) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(com.videvelopers.app.resources.R.string.default_error), Toast.LENGTH_SHORT).show();
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
