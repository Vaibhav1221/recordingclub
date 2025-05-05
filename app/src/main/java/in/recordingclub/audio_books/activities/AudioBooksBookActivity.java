package in.recordingclub.audio_books.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.admin.audio_books_manager.AudioBooksManagerActivity;
import in.recordingclub.audio_books.AudioBooksActivity;
import in.recordingclub.audio_books.adapters.BooksAdapter;
import in.recordingclub.databinding.AudioBooksBookActivityBinding;

public class AudioBooksBookActivity extends AppCompatActivity {

    private AudioBooksBookActivityBinding binding;
    private AppInitializer initializer;
    private MenuItem menuItem;
    private String book_category_name = "";
    private String user_email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), AudioBooksBookActivity.this);
        binding = AudioBooksBookActivityBinding.inflate(getLayoutInflater());
        setTitle("Books");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");
        try {
            Intent intent = getIntent();
            book_category_name = intent.getStringExtra("book_category_name");
            getBooks();
        } catch (Exception e) {

        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            user_email = account.getEmail();
        }
        getAccountStatus();
    }

    private void getAccountStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().account_get_info, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int user_role = jsonObject.getInt("user_role");
                    if (user_role == 1) {
                        menuItem.setVisible(true);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_email_address", user_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getBooks() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Getting", "Getting books...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_books_by_category, new Response.Listener<String>() {
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
                        BooksAdapter adapter = new BooksAdapter(AudioBooksBookActivity.this, books, initializer, new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return false;
                            }
                        });
                        binding.audioBooksBookLvBooks.setAdapter(adapter);
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "There are " + jsonArray.length() + " books.", Toast.LENGTH_SHORT).show();
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
                params.put("book_category_name", book_category_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), AudioBooksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.refresh_books) {
            getBooks();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.books_activity_menus, menu);
        menuItem = menu.findItem(R.id.book_manager);
        menuItem.setVisible(false);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getApplicationContext(), AudioBooksManagerActivity.class));
                return true;
            }
        });
        return true;
    }
}
