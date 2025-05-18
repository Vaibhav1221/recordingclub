package in.recordingclub.books;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.google.firebase.auth.FirebaseAuth;
import com.videvelopers.app.vuh.AppInitializer;
import com.videvelopers.app.vuh.app_components.AppCore;

import in.recordingclub.R;
import in.recordingclub.audio_books.SendFeedback;
import in.recordingclub.models.ChapterModel;

import org.json.*;
import java.util.*;

public class ChapterFragment extends Fragment {

    private static final String ARG_BOOK_ID = "book_id";
    private static final String ARG_BOOK_NAME = "book_name";

    private int bookId;
    private String bookName;
    private TextView textViewBookName;
    private RecyclerView recyclerView;
    private List<ChapterModel> chapterList = new ArrayList<>();
    private AppInitializer initializer;

    // Updated to receive book_id and book_name
    public static ChapterFragment newInstance(int bookId, String bookName) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOOK_ID, bookId);
        args.putString(ARG_BOOK_NAME, bookName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter_fragment, container, false);
        initializer = new AppInitializer(getContext(), (AppCompatActivity) getActivity());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");
        getActivity().setTitle("Chapters");
        textViewBookName = view.findViewById(R.id.textViewBookName);
        recyclerView = view.findViewById(R.id.recyclerViewChapters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            bookId = getArguments().getInt(ARG_BOOK_ID);
            bookName = getArguments().getString(ARG_BOOK_NAME);

            textViewBookName.setText(bookName);
            fetchChapters(bookId);
        }

        return view;
    }

    private void fetchChapters(int bookId) {
        String url = "https://api.recordingclub.in/audio_books/fetch_chapters.php?book_id=" + bookId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("status")) {
                            JSONArray data = json.getJSONArray("data");
                            chapterList.clear();
                            String[] chapter_name = new String[data.length()];
                            String[] chapter_link = new String[data.length()];
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                chapter_name[i] = obj.getString("chapter_name");
                                chapter_link[i] = obj.getString("file_path");
                                chapterList.add(new ChapterModel(
                                        obj.getString("chapter_name"),
                                        obj.getString("file_path")
                                ));
                            }

                            ChapterAdapter adapter = new ChapterAdapter(chapterList, chapter_name, chapter_link, bookName, getActivity());
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "No chapters found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
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
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
                params.put("book", fn_book);
                params.put("email", fn_email);
                return params;
            }
        };
        AppCore.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chapters_activity_menus, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
        } else if (id == R.id.send_feedback_menu) {
            Intent intent = new Intent(getContext(), SendFeedback.class);
            intent.putExtra("book_name", bookName);
            getActivity().startActivity(intent);
        } else if (id == R.id.m2_add_library) {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            addToLibrary(bookName, email);
        } else if (id == R.id.refresh_chaptrs) {
            fetchChapters(bookId);
        }
        return true;
    }

}
