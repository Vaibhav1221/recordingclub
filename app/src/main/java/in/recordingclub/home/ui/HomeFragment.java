package in.recordingclub.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import in.recordingclub.R;
import in.recordingclub.databinding.HomeFragmentBinding;
import in.recordingclub.home.HomeMyLibrary;

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        HomeAdapter homeAdapter = new HomeAdapter(getActivity(), getResources().getStringArray(R.array.main_features));
        binding.homeLvFeatures.setAdapter(homeAdapter);
        setHasOptionsMenu(true);
        setMenuVisibility(true);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.home_activity_menus, menu);
        MenuItem menuItem = menu.findItem(R.id.hm_library);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                startActivity(new Intent(getContext(), HomeMyLibrary.class));
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}