package in.recordingclub.admin.newspaper_manager;

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
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.databinding.CreateNewspaperActivityBinding;

public class CreateNewspaperActivity extends AppCompatActivity implements View.OnClickListener {

    private CreateNewspaperActivityBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), CreateNewspaperActivity.this);
        binding = CreateNewspaperActivityBinding.inflate(getLayoutInflater());
        setTitle("Create Newspaper");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Newspaper Manager. Back");
        binding.createNewspaperTlNewspaperName.setHint(getResources().getString(R.string.newspaper_name));
        binding.createNewspaperBtnNext.setOnClickListener(this::onClick);
        binding.createNewspaperBtnClear.setOnClickListener(this::onClick);
    }

    private void setClear() {
        binding.createNewspaperEtNewspaperName.setText("");
    }

    private void setCreateNewspaper(String newspaper_name) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Creating", "Creating newspaper ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().newspaper_set_newspaper, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        setClear();
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
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
        if (id == binding.createNewspaperBtnClear.getId()) {
            setClear();
            Toast.makeText(getApplicationContext(), "Informations are cleared.", Toast.LENGTH_SHORT).show();
        } else if (id == binding.createNewspaperBtnNext.getId()) {
            String tmp1 = binding.createNewspaperEtNewspaperName.getText().toString();
            if (tmp1.isEmpty()) {
                initializer.getAppFunctions().setAlertDialogForNoInformations();
            } else {
                setCreateNewspaper(tmp1);
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
