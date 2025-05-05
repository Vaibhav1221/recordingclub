package in.recordingclub.admin.audio_books_manager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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

import in.recordingclub.databinding.DeleteAudioBookBinding;

public class DeleteAudioBook extends AppCompatActivity {

    private DeleteAudioBookBinding binding;
    private AppInitializer initializer;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private String book_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeleteAudioBookBinding.inflate(getLayoutInflater());
        setTitle("Delete Book");
        setContentView(binding.getRoot());
        initializer = new AppInitializer(getApplicationContext(), DeleteAudioBook.this);
        getBooks();
        arrayList = new ArrayList<String>();
        arrayList.add("Select Book :");
        binding.deleteBookSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    book_name = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        binding.deleteBookDn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBook(book_name);
            }
        });

    }

    private void getBooks() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_get_books, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray jsonArray = jsonObject.getJSONArray("books");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrayList.add(jsonArray.getString(i));
                        }
                        String[] books = new String[arrayList.size()];
                        for (int i = 0; i < arrayList.size(); i++) {
                            books[i] = arrayList.get(i);
                        }
                        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, books) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };
                        binding.deleteBookSp.setAdapter(arrayAdapter);
                    } else {
                        String msg = jsonObject.getString("msg");
                        Toast.makeText(DeleteAudioBook.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DeleteAudioBook.this, "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DeleteAudioBook.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void deleteBook(String fn_book_name) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Please Wait...", "Deleting Book.");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().delete_audio_book, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(DeleteAudioBook.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(DeleteAudioBook.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(DeleteAudioBook.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("book_title", fn_book_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }


}