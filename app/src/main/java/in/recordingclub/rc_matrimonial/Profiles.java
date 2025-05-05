package in.recordingclub.rc_matrimonial;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.recordingclub.R;
import in.recordingclub.databinding.ProfilesActivityBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Profiles extends AppCompatActivity {

    private final int rc = 13;
    private ProfilesActivityBinding binding;
    private AppInitializer initializer;
    private ProfilesAdapter adapter;
    private String title, fileObject;
    private String name, email, profile;
    private int role;
    private MaterialTextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ProfilesActivityBinding.inflate(getLayoutInflater());
        setTitle("Hamrahi.com");
        setContentView(binding.getRoot());
        initializer = new AppInitializer(getApplicationContext(), Profiles.this);
        getProfiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.matrimonial_profiles_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mp_upload) {
            requestProfileLayout();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == rc && resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();
                fileObject = initializer.getAppFunctions().getPathFromUri(uri);
                File file = new File(fileObject);
                title = file.getName();
                tv.setText(title);
                tv.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, rc);
    }

    private void getProfiles() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Getting Profiles.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().get_profiles, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("profiles");
                    String[] m_title = new String[jsonArray.length()];
                    String[] m_file = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                        m_title[i] = jsonObject2.getString("title");
                        m_file[i] = jsonObject2.getString("url");
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        name = firebaseAuth.getCurrentUser().getDisplayName();
                        email = firebaseAuth.getCurrentUser().getEmail();
                        getUserRole(email);
                    }
                    adapter = new ProfilesAdapter(Profiles.this, m_title, m_file, new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            profile = m_title[adapter.getPosition()];
                            if (role == 1) {
                                initializer.getAppFunctions().setDisplayAlertDialog("Delete profile?", "It will delete this profile permanently.", "Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteProfile();
                                    }
                                });
                            } else {
                                initializer.getAppFunctions().setDisplayAlertDialog("Show Interest", "Request To Show Interest On This Profile.", "Request", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendRequest();
                                    }
                                });
                            }
                            return true;
                        }
                    });
                    binding.profilesListView.setAdapter(adapter);
                    initializer.getAppFunctions().setHideProgressDialog();
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(Profiles.this, "Error In Response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(getApplicationContext(), "Some Thing Is Rong.", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void sendRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().sendRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                } catch (Exception e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Profiles.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("profile", profile);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void sendProfileRequest(String fn_title, String fn_file) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Creating", "Creating post ...");
        File file = new File(fn_file);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "in.recordingclub.provider", file);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        String file_name = file.getName();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", fn_title)
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .addFormDataPart("file", file_name, RequestBody.create(file, MediaType.parse(mime)))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(initializer.getURLHelpers().profileUploadRequest)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
                .callTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Profiles.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Profiles.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            boolean error = jsonObject.getBoolean("error");
                            String msg = jsonObject.getString("msg");
                            if (!error) {
                                initializer.getAppFunctions().setHideProgressDialog();
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            } else {
                                initializer.getAppFunctions().setHideProgressDialog();
                                initializer.getAppFunctions().setAlertDialogForErrorFromServer(msg);
                            }
                        } catch (Exception e) {
                            initializer.getAppFunctions().setHideProgressDialog();
//                            initializer.getAppFunctions().setAlertDialogForResponseError();
                            initializer.getAppFunctions().setAlertDialogForErrorFromServer(e.getMessage());
                        }
                    }
                });
            }
        });
    }

    private void requestProfileLayout() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Profiles.this);
        builder.setCancelable(true);
        builder.setTitle("Request To Upload Profile");
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.profile_upload_request_layout, null);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(com.videvelopers.app.resources.R.color.background_color);
        tv = view.findViewById(R.id.pur_tv);
        MaterialButton select = view.findViewById(R.id.pur_select);
        MaterialButton upload = view.findViewById(R.id.pur_upload);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPicker();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                name = firebaseAuth.getCurrentUser().getDisplayName();
                email = firebaseAuth.getCurrentUser().getEmail();
                sendProfileRequest(title, fileObject);
            }
        });
    }

    private void deleteProfile() {
        initializer.getAppFunctions().setShowProgressDialogWithTitle("Deleting...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().delete_profile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(Profiles.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(Profiles.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(Profiles.this, "Error In Response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(Profiles.this, "Error In Networking", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", profile);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getUserRole(String fn_email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().account_get_info, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    role = jsonObject.getInt("user_role");
                } catch (Exception e) {
                    Toast.makeText(Profiles.this, "Error In Response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Profiles.this, "Error In Networking", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email_address", fn_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }
}