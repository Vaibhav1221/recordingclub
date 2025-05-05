package in.recordingclub.home.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import in.recordingclub.databinding.PrivacyPolicyFragmentBinding;

public class PrivacyPolicyFragment extends Fragment {

    private PrivacyPolicyFragmentBinding binding = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = PrivacyPolicyFragmentBinding.inflate(inflater, container, false);
        binding.privacyPolicyWv.loadUrl("https://recordingclub.in/privacy_policy.html");
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
