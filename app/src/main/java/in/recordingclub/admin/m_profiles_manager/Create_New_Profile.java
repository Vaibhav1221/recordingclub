package in.recordingclub.admin.m_profiles_manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.videvelopers.app.vuh.AppInitializer;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import in.recordingclub.databinding.CreateNewProfileActivityBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Create_New_Profile extends AppCompatActivity implements View.OnClickListener {

    private final int request_file_code = 21;
    private CreateNewProfileActivityBinding binding;
    private AppInitializer initializer;
    private String profile_name;
    private String profile_title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateNewProfileActivityBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), Create_New_Profile.this);
        setTitle("Create New Profile");
        setContentView(binding.getRoot());
        binding.createProfileTitle.setHint("Type profile name :");
        binding.createSelectFile.setOnClickListener(this::onClick);
        binding.createUploadBtn.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.createSelectFile.getId()) {
            setFilePic();
        } else if (id == binding.createUploadBtn.getId()) {
            profile_title = binding.createProfileTitle.getText().toString();
            addNewProfile(profile_title, profile_name);

        }
    }

    private void addNewProfile(String fn_title, String fn_file) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Creating", "Creating post ...");
        File file = new File(fn_file);
        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "in.recordingclub.provider", file);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        String file_name = file.getName();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", fn_title)
                .addFormDataPart("file", file_name, RequestBody.create(file, MediaType.parse(mime)))
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(initializer.getURLHelpers().add_profile)
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
                Create_New_Profile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull okhttp3.Response response) throws IOException {
                Create_New_Profile.this.runOnUiThread(new Runnable() {
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

    private void setFilePic() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, request_file_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_file_code) {
            try {
                profile_name = initializer.getAppFunctions().getPathFromUri(data.getData());
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error While Selecting The File.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}