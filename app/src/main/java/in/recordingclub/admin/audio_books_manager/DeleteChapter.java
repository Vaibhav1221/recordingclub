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
import com.android.volley.Request.Method;
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

import in.recordingclub.databinding.DeleteChapterBinding;


public class DeleteChapter extends AppCompatActivity {

    private DeleteChapterBinding binding;
    private AppInitializer initializer;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayAdapter<String> arrayAdapter2;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayList2;
    private String book = "", chapter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeleteChapterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Delete Chapter");
        initializer = new AppInitializer(getApplicationContext(), DeleteChapter.this);
        arrayList = new ArrayList<String>();
        arrayList2 = new ArrayList<String>();
        arrayList.add("Select Book:");
        arrayList2.add("Select Chapter:");
        getBooks();
        binding.deleteChapterSp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                book = parent.getItemAtPosition(position).toString();
                getChapters(book);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(DeleteChapter.this, "Please Select Book.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.deleteChapterSp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chapter = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(DeleteChapter.this, "Please Select Chapter.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.deleteChapterBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book.isEmpty() || chapter.isEmpty()) {
                    Toast.makeText(DeleteChapter.this, "Please Select Both Fields.", Toast.LENGTH_SHORT).show();
                } else {
                    deleteChapter(book, chapter);
                }
            }
        });

    }

    private void deleteChapter(String fn_book_name, String fn_chapter_name) {
        initializer.getAppFunctions().setShowProgressDialogWithTitle("Deleting...");
        StringRequest stringRequest = new StringRequest(Method.POST, initializer.getURLHelpers().delete_chapter, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");
                    String msg = jsonObject.getString("msg");
                    if (!error) {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(DeleteChapter.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        initializer.getAppFunctions().setHideProgressDialog();
                        Toast.makeText(DeleteChapter.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    initializer.getAppFunctions().setHideProgressDialog();
                    Toast.makeText(DeleteChapter.this, "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                initializer.getAppFunctions().setHideProgressDialog();
                Toast.makeText(DeleteChapter.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("book_name", fn_book_name);
                params.put("chapter_name", fn_chapter_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getChapters(String fn_book_name) {
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
                        arrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, title);
                        binding.deleteChapterSp2.setAdapter(arrayAdapter2);
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
                params.put("book_title", fn_book_name);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    private void getBooks() {
        StringRequest stringRequest = new StringRequest(Method.POST, initializer.getURLHelpers().audio_books_get_books, new Response.Listener<String>() {
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
                        binding.deleteChapterSp1.setAdapter(arrayAdapter);
                    } else {
                        String msg = jsonObject.getString("msg");
                        Toast.makeText(DeleteChapter.this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(DeleteChapter.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DeleteChapter.this, "Error In Networking.", Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }


}