package in.recordingclub.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import in.recordingclub.databinding.ShareAppBinding;

public class ShareApp extends Fragment {

    private ShareAppBinding binding;
    private int activityResultCode = 1212;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ShareAppBinding.inflate(inflater, container, false);
        shareApp();
        if (activityResultCode == 0) {
            getActivity().onBackPressed();
        }
        return binding.getRoot();
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String app_link = "https://play.google.com/store/apps/details?id=in.recordingclub";
        intent.putExtra(Intent.EXTRA_TEXT, app_link);
        Intent modIntent = Intent.createChooser(intent, "Share Via..");
        startActivity(modIntent);
        activityResultCode = 0;
    }

}