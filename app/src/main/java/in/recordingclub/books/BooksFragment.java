package in.recordingclub.books;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import in.recordingclub.R;
import in.recordingclub.admin.audio_books_manager.AudioBooksManagerActivity;
import in.recordingclub.models.BookModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BooksFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private int categoryId;
    private MenuItem menuItem;
    private AppInitializer initializer;
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<BookModel> bookList;

    public BooksFragment() {}

    public static BooksFragment newInstance(int categoryId) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Books");
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        }
        bookList = new ArrayList<>();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(in.recordingclub.R.layout.books_fragment, container, false);
initializer = new AppInitializer(getContext(), (AppCompatActivity) getActivity());
initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");
        getActivity().setTitle("Books");
        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookAdapter(bookList, getActivity());
        recyclerView.setAdapter(adapter);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
if (account != null) {
    String user_email = account.getEmail();
    getAccountStatus(user_email);
}

        loadBooks(categoryId);

        return view;
    }

    private void loadBooks(int categoryId) {
        String url = "https://api.recordingclub.in/audio_books/fetch_books.php?category_id=" + categoryId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray books = response.getJSONArray("books");
                            bookList.clear();
                            for (int i = 0; i < books.length(); i++) {
                                JSONObject book = books.getJSONObject(i);
                                String bookName = book.getString("book_name");
                                int bookID = book.getInt("id");
                                bookList.add(new BookModel(bookID, bookName));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No books found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private void getAccountStatus(String user_email) {
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
                    Toast.makeText(getContext(), "Error In Response.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error In Networking.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.books_activity_menus, menu);
        menuItem = menu.findItem(R.id.book_manager);
        menuItem.setVisible(false);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(getContext(), AudioBooksManagerActivity.class));
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
        } else if (id == R.id.refresh_books) {
            loadBooks(categoryId);
        }
        return true;
    }

    }
