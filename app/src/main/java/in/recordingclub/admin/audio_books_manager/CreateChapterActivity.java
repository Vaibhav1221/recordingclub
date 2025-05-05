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
import in.recordingclub.databinding.CreateChapterActivityBinding;

public class CreateChapterActivity extends AppCompatActivity implements View.OnClickListener {

    private CreateChapterActivityBinding binding;
    private AppInitializer initializer;
    private String book_title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), CreateChapterActivity.this);
        binding = CreateChapterActivityBinding.inflate(getLayoutInflater());
        setTitle("New Chapter");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Audio Books Manager. Back");
        binding.createChapterTlChapterTitle.setHint(R.string.audio_books_chapter_title);
        binding.createChapterTlChapterFileLink.setHint(R.string.audio_books_chapter_file_link);
        binding.createChapterBtnNext.setOnClickListener(this::onClick);
        getBooks();
        binding.createChapterSpBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    book_title = "";
                    book_title = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getApplicationContext(), "Selected book: " + book_title + ".", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBooks() {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Getting", "Getting books...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_get_books, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    if (!error) {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        arrayList.add("Book:");
                        JSONArray jsonArray = jsonObject.getJSONArray("books");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrayList.add(jsonArray.getString(i));
                        }
                        String[] books = new String[arrayList.size()];
                        for (int i = 0; i < arrayList.size(); i++) {
                            books[i] = arrayList.get(i);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, books) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }
                        };
                        binding.createChapterSpBook.setAdapter(adapter);
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
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void setChapter(String fn_title, String fn_file_link) {
        initializer.getAppFunctions().setShowProgressDialogWithTitleAndMessage("Creating", "Creating chapter ...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().audio_books_create_chapter, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        binding.createChapterEtChapterTitle.setText("");
                        binding.createChapterEtChapterFileLink.setText("");
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
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
                params.put("chapter_title", fn_title);
                params.put("chapter_file_link", fn_file_link);
                params.put("book_title", book_title);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.createChapterBtnNext.getId()) {
            String tmp1 = binding.createChapterEtChapterTitle.getText().toString();
            String tmp2 = binding.createChapterEtChapterFileLink.getText().toString();
            if (tmp1.isEmpty() || tmp2.isEmpty() || book_title.isEmpty()) {
                initializer.getAppFunctions().setAlertDialogForNoInformations();
            } else {
                setChapter(tmp1, tmp2);
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
