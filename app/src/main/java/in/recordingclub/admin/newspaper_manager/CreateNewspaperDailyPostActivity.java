package in.recordingclub.admin.newspaper_manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.recordingclub.R;
import in.recordingclub.databinding.CreateNewspaperDailyPostActivityBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class CreateNewspaperDailyPostActivity extends AppCompatActivity implements View.OnClickListener {

    private CreateNewspaperDailyPostActivityBinding binding;
    private AppInitializer initializer;
    private ArrayList<String> arrayList;
    private String newspaper_name, newspaper_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), CreateNewspaperDailyPostActivity.this);
        binding = CreateNewspaperDailyPostActivityBinding.inflate(getLayoutInflater());
        setTitle("Upload Newspaper");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Newspaper Manager. Back");
        arrayList = new ArrayList<String>();
        arrayList.add("Select newspaper:");
        binding.createNewspaperDailyPostTlNewspaperTitle.setHint(getResources().getString(R.string.newspaper_title));
        getNewspapers();
        binding.createNewspaperDailyPostBtnClear.setOnClickListener(this::onClick);
        binding.createNewspaperDailyPostBtnNext.setOnClickListener(this::onClick);
        binding.createNewspaperDailyPostBtnChooseFile.setOnClickListener(this::onClick);
        binding.createNewspaperDailyPostSpNewspapers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    newspaper_name = "";
                    newspaper_name = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(), "Your selected newspaper is " + newspaper_name + ".", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getNewspapers() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Loading", "Loading newspapers ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().newspaper_get_newspapers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("newspapers");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrayList.add(jsonArray.getString(i));
                        }
                        String[] newspapers = new String[arrayList.size()];
                        for (int i = 0; i < arrayList.size(); i++) {
                            newspapers[i] = arrayList.get(i);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, newspapers) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };
                        binding.createNewspaperDailyPostSpNewspapers.setAdapter(arrayAdapter);
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "There are " + jsonArray.length() + " newspapers available.", Toast.LENGTH_SHORT).show();
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
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void setNewspaperDailyPost(String fn_newspaper_title, String fn_newspaper_file) {
        File xfile = new File(fn_newspaper_file);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "in.recordingclub.provider", xfile);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        String file_name = xfile.getName();
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Uploading...", "Uploading " + file_name);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("newspaper_title", fn_newspaper_title)
                .addFormDataPart("newspaper_name", newspaper_name)
                .addFormDataPart("newspaper_file", file_name, RequestBody.create(xfile, MediaType.parse(mime)))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(initializer.getURLHelpers().newspapers_set_newspaper_daily_post)
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
                CreateNewspaperDailyPostActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                CreateNewspaperDailyPostActivity.this.runOnUiThread(new Runnable() {
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

    private void setFilePick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, getResources().getInteger(com.videvelopers.app.resources.R.integer.default_activity_request_code));
    }

    private void setClear() {
        binding.createNewspaperDailyPostEtNewspaperTitle.setText("");
        newspaper_file = "";
        newspaper_name = "";
        binding.createNewspaperDailyPostSpNewspapers.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.createNewspaperDailyPostBtnClear.getId()) {
            setClear();
            Toast.makeText(getApplicationContext(), "Informations are cleared.", Toast.LENGTH_SHORT).show();
        } else if (id == binding.createNewspaperDailyPostBtnNext.getId()) {
            String tmp1 = binding.createNewspaperDailyPostEtNewspaperTitle.getText().toString();
            if (tmp1.isEmpty() || newspaper_name.isEmpty() || newspaper_file.isEmpty()) {
                initializer.getAppFunctions().setAlertDialogForNoInformations();
            } else {
                setNewspaperDailyPost(tmp1, newspaper_file);
            }
        } else if (id == binding.createNewspaperDailyPostBtnChooseFile.getId()) {
            setFilePick();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == getResources().getInteger(com.videvelopers.app.resources.R.integer.default_activity_request_code)) {
            try {
                newspaper_file = initializer.getAppFunctions().getPathFromUri(data.getData());
                File file = new File(newspaper_file);
                String tmp_file_name = file.getName();
                String file_name_without_extention = tmp_file_name.replaceAll(".mp3", "").replace(".wav", "").replace(".ogg", "").replace(".opus", "").replace(".m4a", "").replace(".ptt", "");
                binding.createNewspaperDailyPostBtnNext.setFocusableInTouchMode(true);
                binding.createNewspaperDailyPostBtnNext.requestFocus();
                binding.createNewspaperDailyPostBtnNext.requestFocusFromTouch();
                binding.createNewspaperDailyPostEtNewspaperTitle.setText(file_name_without_extention);
            } catch (Exception e) {

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
