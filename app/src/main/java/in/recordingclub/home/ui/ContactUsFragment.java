package in.recordingclub.home.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import in.recordingclub.databinding.ContactUsFragmentBinding;

public class ContactUsFragment extends Fragment implements View.OnClickListener {

    private ContactUsFragmentBinding binding = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ContactUsFragmentBinding.inflate(inflater, container, false);
        binding.contactUsBtnWebsite.setOnClickListener(this::onClick);
        binding.contactUsBtnCp.setOnClickListener(this::onClick);
        binding.contactUsBtnCl.setOnClickListener(this::onClick);
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.contactUsBtnWebsite.getId()) {
            Intent i1 = new Intent(Intent.ACTION_VIEW);
            i1.setData(Uri.parse("https://recordingclub.in"));
            startActivity(i1);
        } else if (id == binding.contactUsBtnCp.getId()) {
            Intent i2 = new Intent(Intent.ACTION_DIAL);
            i2.setData(Uri.parse("tel:+917737868659"));
            startActivity(i2);
        } else if (id == binding.contactUsBtnCl.getId()) {
            Intent i3 = new Intent(Intent.ACTION_DIAL);
            i3.setData(Uri.parse("tel:01453566471"));
            startActivity(i3);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
