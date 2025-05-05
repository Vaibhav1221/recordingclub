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

import in.recordingclub.databinding.GetAllFreeUsersBinding;

public class GetAllFreeUsers extends AppCompatActivity {

    private GetAllFreeUsersBinding binding;
    private AppInitializer initializer;
    private DMAdaptor dmAdaptor;
    private ArrayList<String> names, emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GetAllFreeUsersBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), GetAllFreeUsers.this);
        setContentView(binding.getRoot());
        setTitle("All Free Users");
        getFreeUsers();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query.toLowerCase());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText.toLowerCase());
                return true;
            }
        });
    }

    private void getFreeUsers() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().getAllFreeUsers, new Response.Listener<String>() {
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
                    dmAdaptor = new DMAdaptor(getApplicationContext(), GetAllFreeUsers.this, names, emails);
                    binding.listViewGafu.setAdapter(dmAdaptor);
                    Toast.makeText(GetAllFreeUsers.this, "Total free users is " + jsonArray.length(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GetAllFreeUsers.this, "Error in networking", Toast.LENGTH_SHORT).show();
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

            dmAdaptor = new DMAdaptor(getApplicationContext(), GetAllFreeUsers.this, filteredNames, filteredEmails);
            binding.listViewGafu.setAdapter(dmAdaptor);
            dmAdaptor.notifyDataSetChanged(); // Notify adapter about the changes
        } else {
            dmAdaptor = new DMAdaptor(getApplicationContext(), GetAllFreeUsers.this, filteredNames, filteredEmails);
            binding.listViewGafu.setAdapter(dmAdaptor);
            dmAdaptor.notifyDataSetChanged(); // Notify adapter about the changes
        }
    }


}