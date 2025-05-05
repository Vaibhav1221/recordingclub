package in.recordingclub.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.recordingclub.databinding.GetAllUsersActivityBinding;

public class GetAllUsers extends AppCompatActivity {

    private GetAllUsersActivityBinding binding;
    private AppInitializer initializer;
    private DMAdaptor dmAdaptor;
    private ArrayList<String> names, emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GetAllUsersActivityBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), GetAllUsers.this);
        setTitle("All User");
        setContentView(binding.getRoot());
        getAllUsers();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });
    }

    private void getAllUsers() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().get_all_users, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("names");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("emails");
                    names = new ArrayList<String>();
                    emails = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        names.add(jsonArray.getString(i));
                        emails.add(jsonArray1.getString(i));
                    }
                    initializer.getAppFunctions().setHideProgressDialog();
                    DMAdaptor dmAdaptor = new DMAdaptor(getApplicationContext(), GetAllUsers.this, names, emails);
                    binding.listViewGau.setAdapter(dmAdaptor);
                    Toast.makeText(GetAllUsers.this, "Total Users " + jsonArray.length(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(GetAllUsers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(GetAllUsers.this, "Error In Networking", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void performSearch(String searchQuery) {
        ArrayList<String> filteredNames = new ArrayList<>();
        ArrayList<String> filteredEmails = new ArrayList<>();
        if (dmAdaptor != null) {


            for (int i = 0; i < names.size(); i++) {
                if (emails.get(i).toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredNames.add(names.get(i));
                    filteredEmails.add(emails.get(i));
                }
            }

            dmAdaptor = new DMAdaptor(getApplicationContext(), GetAllUsers.this, filteredNames, filteredEmails);
            binding.listViewGau.setAdapter(dmAdaptor);
            dmAdaptor.notifyDataSetChanged(); // Notify adapter about the changes
        } else {
            dmAdaptor = new DMAdaptor(getApplicationContext(), GetAllUsers.this, filteredNames, filteredEmails);
            binding.listViewGau.setAdapter(dmAdaptor);
            dmAdaptor.notifyDataSetChanged(); // Notify adapter about the changes
        }
    }


}