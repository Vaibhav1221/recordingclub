package in.recordingclub.audio_books.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import org.json.JSONArray;
import org.json.JSONObject;

import in.recordingclub.audio_books.adapters.BooksAdapter;
import in.recordingclub.databinding.RecentlyUploadedBooksActivityBinding;

public class RecentlyUploadedBooksActivity extends AppCompatActivity {

    private RecentlyUploadedBooksActivityBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecentlyUploadedBooksActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Recently uploaded");
        initializer = new AppInitializer(getApplicationContext(), RecentlyUploadedBooksActivity.this);
        getRecentlyUploadedBooks();
    }

    private void getRecentlyUploadedBooks() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, initializer.getURLHelpers().get_recently_uploaded_books, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("books");
                    String[] books = new String[jsonArray.length()];

                    for (int i = 0; i < jsonArray.length(); i++) {
                        books[i] = jsonArray.getString(i);
                    }
                    BooksAdapter booksAdapter = new BooksAdapter(RecentlyUploadedBooksActivity.this, books, initializer, new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                    binding.listViewRub.setAdapter(booksAdapter);
                } catch (Exception e) {
                    Toast.makeText(RecentlyUploadedBooksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RecentlyUploadedBooksActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }


}