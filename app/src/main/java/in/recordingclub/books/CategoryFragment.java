    package in.recordingclub.books;

    import android.content.Intent;
    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;
    import com.android.volley.*;
    import com.android.volley.toolbox.*;
    import com.videvelopers.app.vuh.AppInitializer;

    import org.json.*;
    import android.view.*;
    import android.widget.Toast;
    import java.util.*;

    import in.recordingclub.audio_books.activities.RecentlyUploadedBooksActivity;
    import in.recordingclub.databinding.CategoryFragmentBinding;
    import in.recordingclub.models.CategoryModel;
    import in.recordingclub.R;

    public class CategoryFragment extends Fragment {

        private static final String ARG_PARENT_ID = "parent_id";
        private int parentId, subCategoryID;
        private CategoryFragmentBinding binding;


        public CategoryFragment() {}

        public static CategoryFragment newInstance(int parentId) {
            CategoryFragment fragment = new CategoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_PARENT_ID, parentId);
            fragment.setArguments(args);
            return fragment;
        }

        private RecyclerView recyclerView;
        private List<CategoryModel> categoryList = new ArrayList<>();
        private String type = "";
        private AppInitializer initializer;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = CategoryFragmentBinding.inflate(inflater, container, false);
            getActivity().setTitle("Audio books categories");
            initializer = new AppInitializer(getContext(), (AppCompatActivity) getActivity());
            recyclerView = binding.recyclerViewCategory;
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
initializer.getAppActionBar().setCustomActionBarWithBackButton("Back");
            if (getArguments() != null) {
                parentId = getArguments().getInt(ARG_PARENT_ID, -1);
            }

            fetchCategory(parentId);

            return binding.getRoot();
        }

        private void fetchCategory(int parentId) {
            String url = "https://api.recordingclub.in/audio_books/fetch_categories.php";
            if (parentId != -1) {
                url += "?parent_id=" + parentId;
            }

            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("status")) {
                                JSONArray data = json.getJSONArray("data");

                                categoryList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    categoryList.add(new CategoryModel(
                                            obj.getInt("id"),
                                            obj.getString("name")
                                    ));
                                }

                                CategoryAdapter adapter = new CategoryAdapter(categoryList, category -> {
openNextFragment(category.getId());
});

                                recyclerView.setAdapter(adapter);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(requireContext()).add(request);
        }

        private void openNextFragment(int categoryId) {
            String checkUrl = "https://api.recordingclub.in/audio_books/fetch_categories.php?parent_id=" + categoryId;

            StringRequest checkRequest = new StringRequest(Request.Method.GET, checkUrl,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray data = json.getJSONArray("data");

                            if (data.length() == 0) {
                                // No more categories — open books fragment
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, BooksFragment.newInstance(categoryId))
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                // More subcategories — open next category fragment
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, CategoryFragment.newInstance(categoryId))
                                        .addToBackStack(null)
                                        .commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(requireContext()).add(checkRequest);
        }

        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.sub_categories_menus, menu);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
            } else if (id == R.id.refresh_sub_categories_menu) {
                fetchCategory(parentId);
            } else if (id == R.id.recently_uploaded_menu) {
                getActivity().startActivity(new Intent(getContext(), RecentlyUploadedBooksActivity.class));
            }
            return true;
        }


    }
