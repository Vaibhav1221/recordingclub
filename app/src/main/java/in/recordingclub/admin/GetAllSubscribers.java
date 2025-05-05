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

import in.recordingclub.databinding.GetAllSubscribersBinding;


public class GetAllSubscribers extends AppCompatActivity {

    private GetAllSubscribersBinding binding;

    private AppInitializer initializer;
    private SubscribersAdaptor subscribersAdaptor;
    private ArrayList<String> emails, phones, subscribed_date, renew_date, d, m, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GetAllSubscribersBinding.inflate(getLayoutInflater());
        initializer = new AppInitializer(getApplicationContext(), GetAllSubscribers.this);
        setTitle("Subscribers");
        setContentView(binding.getRoot());
        getAllSubscribers();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

    }

    private void getAllSubscribers() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().get_all_subscribers, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("emails");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("phones");
                    JSONArray jsonArray2 = jsonObject.getJSONArray("subscribed_date");
                    JSONArray jsonArray3 = jsonObject.getJSONArray("renew_date");
                    JSONArray jsonArray4 = jsonObject.getJSONArray("d");
                    JSONArray jsonArray5 = jsonObject.getJSONArray("m");
                    JSONArray jsonArray6 = jsonObject.getJSONArray("y");
                    emails = new ArrayList<String>();
                    phones = new ArrayList<String>();
                    subscribed_date = new ArrayList<String>();
                    renew_date = new ArrayList<String>();
                    d = new ArrayList<String>();
                    m = new ArrayList<String>();
                    y = new ArrayList<String>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        emails.add(jsonArray.getString(i));
                        phones.add(jsonArray1.getString(i));
                        subscribed_date.add(jsonArray2.getString(i));
                        renew_date.add(jsonArray3.getString(i));
                        d.add(jsonArray4.getString(i));
                        m.add(jsonArray5.getString(i));
                        y.add(jsonArray6.getString(i));
                    }
                    subscribersAdaptor = new SubscribersAdaptor(getApplicationContext(), GetAllSubscribers.this, emails, phones, subscribed_date, renew_date, d, m, y);
                    binding.listViewGas.setAdapter(subscribersAdaptor);
                    Toast.makeText(GetAllSubscribers.this, "All subscribers " + emails.size(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(GetAllSubscribers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(GetAllSubscribers.this, "Error In Networking", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void performSearch(String searchQuery) {
        ArrayList<String> filteredEmails = new ArrayList<>();
        ArrayList<String> filteredPhones = new ArrayList<>();
        ArrayList<String> filterredSubscribeDate = new ArrayList<String>();
        ArrayList<String> filteredRenewDate = new ArrayList<String>();
        ArrayList<String> filteredD = new ArrayList<String>();
        ArrayList<String> filteredM = new ArrayList<String>();
        ArrayList<String> filteredY = new ArrayList<String>();

        if (subscribersAdaptor != null) {
            for (int i = 0; i < emails.size(); i++) {
                if (emails.get(i).toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredEmails.add(emails.get(i));
                    filteredPhones.add(phones.get(i));
                    filterredSubscribeDate.add(subscribed_date.get(i));
                    filteredRenewDate.add(renew_date.get(i));
                    filteredD.add(d.get(i));
                    filteredM.add(m.get(i));
                    filteredY.add(y.get(i));
                }
            }

            subscribersAdaptor = new SubscribersAdaptor(getApplicationContext(), GetAllSubscribers.this, filteredEmails, filteredPhones, filterredSubscribeDate, filteredRenewDate, filteredD, filteredM, filteredY);
            binding.listViewGas.setAdapter(subscribersAdaptor);
            subscribersAdaptor.notifyDataSetChanged();
        } else {
            subscribersAdaptor = new SubscribersAdaptor(getApplicationContext(), GetAllSubscribers.this, emails, phones, subscribed_date, renew_date, d, m, y);
            binding.listViewGas.setAdapter(subscribersAdaptor);
            subscribersAdaptor.notifyDataSetChanged(); // Notify adapter about the changes
        }
    }


}