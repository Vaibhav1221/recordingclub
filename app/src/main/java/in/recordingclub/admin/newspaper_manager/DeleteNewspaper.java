package in.recordingclub.admin.newspaper_manager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.recordingclub.databinding.DeleteNewspaperBinding;

public class DeleteNewspaper extends AppCompatActivity {

    private DeleteNewspaperBinding binding;
    private AppInitializer initializer;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private String newspaper_name;
    private MaterialButton materialButton;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeleteNewspaperBinding.inflate(getLayoutInflater());
        setTitle("Delete Newspaper");
        setContentView(binding.getRoot());
        initializer = new AppInitializer(getApplicationContext(), DeleteNewspaper.this);
        arrayList = new ArrayList<String>();
        arrayList.add("Select Newspaper :");
        getNewspapers();
        binding.deleteNewspaperSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    newspaper_name = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.deleteNewspaperDn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNewspaper(newspaper_name);
            }
        });
    }

    private void getNewspapers() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Fetching Newspapers.");
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
                        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, newspapers) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };
                        binding.deleteNewspaperSp.setAdapter(arrayAdapter);
                        initializer.getAppFunctions().setHideProgressDialog();
                    } else {
                        String msg = jsonObject.getString("msg");
                        initializer.getAppFunctions().setHideProgressDialog();
                        initializer.getAppFunctions().setAlertDialogForErrorFromServer(msg);
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(DeleteNewspaper.this, "Error in response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(DeleteNewspaper.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void deleteNewspaper(String fn_newspaper_name) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Deleting Newspaper.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().delete_newspaper, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(DeleteNewspaper.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(DeleteNewspaper.this, msg, Toast.LENGTH_SHORT).show();
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
                initializer.getAppFunctions().setAlertDialogForErrorFromServer("Error In Networking.");
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("newspaper", fn_newspaper_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);

    }


}