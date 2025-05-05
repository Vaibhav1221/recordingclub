package in.recordingclub.audio_books;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
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

import in.recordingclub.R;
import in.recordingclub.audio_books.activities.RecentlyUploadedBooksActivity;
import in.recordingclub.audio_books.adapters.ParentCategoriesAdapters;
import in.recordingclub.databinding.AudioBooksActivityBinding;

public class AudioBooksActivity extends AppCompatActivity {

    private AudioBooksActivityBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), AudioBooksActivity.this);
        binding = AudioBooksActivityBinding.inflate(getLayoutInflater());
        setTitle("Audio Books");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Home. Back");
        getCategories();
    }

    private void getCategories() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Getting", "Getting categories...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_get_root_categories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("categories");
                        String[] categories = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            categories[i] = jsonArray.getString(i);
                        }
                        ParentCategoriesAdapters adapter = new ParentCategoriesAdapters(AudioBooksActivity.this, categories);
                        binding.audioBooksLvParentCategories.setAdapter(adapter);
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "There are " + jsonArray.length() + " categories.", Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = jsonObject.getString("msg");
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
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.books_categories_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.recently_uploaded_menu) {
            startActivity(new Intent(getApplicationContext(), RecentlyUploadedBooksActivity.class));
        }
        return true;
    }
}
