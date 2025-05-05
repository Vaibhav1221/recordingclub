package in.recordingclub.admin.audio_books_manager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.databinding.CreateSubCategoryActivityBinding;


public class CreateSubCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private CreateSubCategoryActivityBinding binding;
    private AppInitializer initializer;
    private String parent_category_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), CreateSubCategoryActivity.this);
        binding = CreateSubCategoryActivityBinding.inflate(getLayoutInflater());
        setTitle("Create Sub Category");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Audio Books Manager. Back");
        binding.createSubCategoryTlCategoryName.setHint(R.string.audio_books_category);
        binding.createSubCategoryBtnNext.setOnClickListener(this::onClick);
        getCategories();
        binding.createSubCategorySpParentCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    parent_category_name = "";
                    parent_category_name = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(), "Selected parent category is " + parent_category_name + ".", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getCategories() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Getting", "Getting categories...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_get_all_categories, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        arrayList.add("Parent category:");
                        JSONArray jsonArray = jsonObject.getJSONArray("categories");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrayList.add(jsonArray.getString(i));
                        }
                        String[] categories = new String[arrayList.size()];
                        for (int i = 0; i < arrayList.size(); i++) {
                            categories[i] = arrayList.get(i);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };
                        binding.createSubCategorySpParentCategories.setAdapter(adapter);
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

    private void setSubCategory(String book_category_name) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Adding", "Adding category ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_create_sub_category, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        binding.createSubCategorySpParentCategories.setSelection(0);
                        parent_category_name = "";
                        binding.createSubCategoryEtCategoryName.setText("");
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        getCategories();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        initializer.getAppFunctions().setAlertDialogForErrorFromServer(msg);
                    }
                } catch (Exception e) {
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
                params.put("book_category_name", book_category_name);
                params.put("parent_category_name", parent_category_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.createSubCategoryBtnNext.getId()) {
            String tmp1 = binding.createSubCategoryEtCategoryName.getText().toString();
            if (tmp1.isEmpty() || parent_category_name.isEmpty()) {
                initializer.getAppFunctions().setAlertDialogForNoInformations();
            } else {
                setSubCategory(tmp1);
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
