package in.recordingclub.audio_books.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.audio_books.adapters.SubCategoriesAdapter;
import in.recordingclub.databinding.AudioBooksSubCategoriesActivityBinding;

public class AudioBooksSubCategoriesActivity extends AppCompatActivity {

    private AudioBooksSubCategoriesActivityBinding binding;
    private AppInitializer initializer;
    private String parent_category_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), AudioBooksSubCategoriesActivity.this);
        binding = AudioBooksSubCategoriesActivityBinding.inflate(getLayoutInflater());
        setTitle("Categories");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");
        if (parent_category_name.isEmpty()) {
            try {
                Intent intent = getIntent();
                parent_category_name = intent.getStringExtra("parent_category_name");
                getCategories();
            } catch (Exception e) {

            }
        } else {
            getCategories();
        }
    }

    private void getCategories() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Getting", "Getting categories...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_get_sub_categories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("categories");
                        if (jsonArray.length() == 0) {
                            Intent intent = new Intent(getApplicationContext(), AudioBooksBookActivity.class);
                            intent.putExtra("book_category_name", parent_category_name);
                            startActivity(intent);
                        } else if (jsonArray.length() > 0) {
                            String[] categories = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                categories[i] = jsonArray.getString(i);
                            }
                            SubCategoriesAdapter adapter = new SubCategoriesAdapter(AudioBooksSubCategoriesActivity.this, categories);
                            binding.audioBooksLvSubCategories.setAdapter(adapter);
                            initializer.getAppFunctions().setHideProgressDialog();
                            Toast.makeText(getApplicationContext(), "There are " + jsonArray.length() + " categories.", Toast.LENGTH_SHORT).show();
                        }
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
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("parent_category_name", parent_category_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sub_categories_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.refresh_sub_categories_menu) {
            getCategories();
        }
        return true;
    }
}
