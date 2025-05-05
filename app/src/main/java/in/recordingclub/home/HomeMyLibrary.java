package in.recordingclub.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.audio_books.adapters.BooksAdapter;
import in.recordingclub.databinding.HomeMyLibraryBinding;

public class HomeMyLibrary extends AppCompatActivity {

    private HomeMyLibraryBinding binding;
    private AppInitializer initializer;
    private String email;
    private int position;
    private BooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeMyLibraryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("My library");
        initializer = new AppInitializer(this, this);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        email = firebaseAuth.getCurrentUser().getEmail();
        getLibraryItems();
    }

    private void getLibraryItems() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().fetch_library_item,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray("books");
                                String[] books = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    books[i] = jsonArray.getString(i);
                                }
                                adapter = new BooksAdapter(HomeMyLibrary.this, books, initializer, new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        position = (int) v.getTag();
                                        initializer.getAppFunctions().setDisplayAlertDialog("Remove book", "Are you sure you want to remove this book from library?", "No", "Yes",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                },
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        removeBook(books[position], email);
                                                    }
                                                });
                                        return true;
                                    }
                                });

                                binding.libraryListView.setAdapter(adapter);
                                Toast.makeText(HomeMyLibrary.this, "You have " + jsonArray.length() + " books in your library.", Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(HomeMyLibrary.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(HomeMyLibrary.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeMyLibrary.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    public void removeBook(String fn_book_name, String fn_user_email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().remove_from_library,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean("error");
                            String msg = jsonObject.getString("msg");
                            if (!error) {
                                Toast.makeText(HomeMyLibrary.this, msg, Toast.LENGTH_SHORT).show();
                                getLibraryItems();
                            } else {
                                Toast.makeText(HomeMyLibrary.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(HomeMyLibrary.this, "Error In Response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeMyLibrary.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("book", fn_book_name);
                params.put("email", fn_user_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }
}
