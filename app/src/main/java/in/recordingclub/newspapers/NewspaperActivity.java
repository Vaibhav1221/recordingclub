package in.recordingclub.newspapers;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import in.recordingclub.databinding.NewspaperManagerActivityBinding;

public class NewspaperActivity extends AppCompatActivity {

    private NewspaperManagerActivityBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), NewspaperActivity.this);
        binding = NewspaperManagerActivityBinding.inflate(getLayoutInflater());
        setTitle("Newspapers");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Home. Back");
        getNewspapers();
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
                        String[] newspapers = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            newspapers[i] = jsonArray.getString(i);
                        }
                        NewspapersAdapter newspapersAdapter = new NewspapersAdapter(NewspaperActivity.this, newspapers);
                        binding.newspaperManagerLv.setAdapter(newspapersAdapter);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
