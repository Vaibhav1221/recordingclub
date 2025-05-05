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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.recordingclub.R;
import in.recordingclub.audio_books.SendFeedback;
import in.recordingclub.audio_books.adapters.ChaptersAdapter;
import in.recordingclub.databinding.AudioBooksChaptersActivityBinding;

public class AudioBooksChaptersActivity extends AppCompatActivity {

    private final boolean subscriber = false;
    private AudioBooksChaptersActivityBinding binding;
    private AppInitializer initializer;
    private String book_title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), AudioBooksChaptersActivity.this);
        binding = AudioBooksChaptersActivityBinding.inflate(getLayoutInflater());
        setTitle("Chapters");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");

        try {
            Intent intent = getIntent();
            book_title = intent.getStringExtra("book_title");
            getChapters();
        } catch (Exception e) {

        }
        MaterialTextView tv = findViewById(R.id.book_title_text_view);
        tv.setText(book_title);
    }


    private void getChapters() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Getting", "Getting chapters ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_get_chapter, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        JSONArray array = jsonObject.getJSONArray("book_info");
                        String[] title = new String[array.length()];
                        String[] file_link = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject book_info = array.getJSONObject(i);
                            title[i] = book_info.getString("chapter_title");
                            file_link[i] = book_info.getString("chapter_file_link");
                        }
                        ChaptersAdapter adapter = new ChaptersAdapter(AudioBooksChaptersActivity.this, title, file_link, book_title);
                        binding.audioBooksChaptersLvChapters.setAdapter(adapter);
                        Toast.makeText(AudioBooksChaptersActivity.this, "Their are " + array.length() + "files in this book.", Toast.LENGTH_SHORT).show();
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), "The book is loaded successfully.", Toast.LENGTH_SHORT).show();
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
                params.put("book_title", book_title);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void addToLibrary(String fn_book, String fn_email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().addToLibrary, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AudioBooksChaptersActivity.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("book", fn_book);
                params.put("email", fn_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.send_feedback_menu) {
            Intent intent = new Intent(getApplicationContext(), SendFeedback.class);
            intent.putExtra("book_name", book_title);
            startActivity(intent);
        } else if (id == R.id.m2_add_library) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            addToLibrary(book_title, email);
        } else if (id == R.id.refresh_chaptrs) {
            getChapters();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chapters_activity_menus, menu);
        return true;
    }

}
