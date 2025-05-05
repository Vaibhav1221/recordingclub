package in.recordingclub.newspapers;

import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.MainActivity;
import in.recordingclub.databinding.NewspaperPostsActivityBinding;

public class NewspaperPostsActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser firebaseUser;
    private AppInitializer initializer;
    private NewspaperPostsActivityBinding binding;
    private String newspaper_name = "";
    private String email = "";
    private boolean subscriber;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), NewspaperPostsActivity.this);
        binding = NewspaperPostsActivityBinding.inflate(getLayoutInflater());
        setTitle("Posts");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Newspapers. Back");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        email = firebaseUser.getEmail();
        try {
            Intent intent = getIntent();
            newspaper_name = intent.getStringExtra("newspaper_name");
            getSubscriptionStatus(email);
            getNewspaperPost();
        } catch (Exception e) {
            newspaper_name = "";
        }
        binding.newspaperPostsBtnViewOlderPosts.setOnClickListener(this::onClick);
    }

    private boolean getSubscriptionStatus(String fn_user_email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().getSubscription, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        boolean status = jsonObject.getBoolean("status");
                        int user_role = jsonObject.getInt("user_role");
                        int expire = jsonObject.getInt("expire");
                        if (user_role == 0) {
                            if (!status) {
                                subscriber = true;
                            }
                        } else {
                            subscriber = false;
                        }
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Error in response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(getApplicationContext(), "Error In Networking", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("email", fn_user_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
        return subscriber;
    }

    private void getNewspaperPost() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Loading", "Lodaing newspaper post ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().newspaper_get_newspaper_daily_post, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("newspaper_daily_posts");
                        String[] newspaper_title = new String[jsonArray.length()];
                        String[] newspaper_file_link = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            newspaper_title[i] = jsonObject2.getString("newspaper_title");
                            newspaper_file_link[i] = jsonObject2.getString("newspaper_file_link");
                        }
                        NewspaperPostsAdapter newspaperPostsAdapter = new NewspaperPostsAdapter(NewspaperPostsActivity.this, newspaper_title, newspaper_file_link, initializer, subscriber);
                        binding.newspaperPostsLv.setAdapter(newspaperPostsAdapter);
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Newspaper posts are loaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        String msg = jsonObject.getString("msg");
                        initializer.getAppFunctions().setAlertDialogForErrorFromServer(msg);
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    initializer.getAppFunctions().setAlertDialogForResponseError();
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
                params.put("newspaper_name", newspaper_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getNewspaperPostByNewspaperName() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Loading", "Lodaing newspaper post ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().newspaper_get_newspaper_daily_post_by_newspaper_name, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("newspaper_daily_posts");
                        String[] newspaper_title = new String[jsonArray.length()];
                        String[] newspaper_file_link = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            newspaper_title[i] = jsonObject2.getString("newspaper_title");
                            newspaper_file_link[i] = jsonObject2.getString("newspaper_file_link");
                        }
                        NewspaperPostsAdapter newspaperPostsAdapter = new NewspaperPostsAdapter(NewspaperPostsActivity.this, newspaper_title, newspaper_file_link, initializer, subscriber);
                        binding.newspaperPostsLv.setAdapter(newspaperPostsAdapter);
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "Newspaper posts are loaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        String msg = jsonObject.getString("msg");
                        initializer.getAppFunctions().setAlertDialogForErrorFromServer(msg);
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    initializer.getAppFunctions().setAlertDialogForResponseError();
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
                params.put("newspaper_name", newspaper_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.newspaperPostsBtnViewOlderPosts.getId()) {
            getNewspaperPostByNewspaperName();
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
