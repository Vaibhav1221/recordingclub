package in.recordingclub.home.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import in.recordingclub.R;
import in.recordingclub.databinding.OurSocialMediaPlatformsFragmentBinding;

public class OurSocialMediaPlatformsFragment extends Fragment {

    private OurSocialMediaPlatformsFragmentBinding binding = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OurSocialMediaPlatformsFragmentBinding.inflate(inflater, container, false);
        OurSocialMediaPlatformsAdapter adapter = new OurSocialMediaPlatformsAdapter(getActivity(), getResources().getStringArray(R.array.social_media_platforms));
        binding.ourSocialMediaPlatformsLv.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
