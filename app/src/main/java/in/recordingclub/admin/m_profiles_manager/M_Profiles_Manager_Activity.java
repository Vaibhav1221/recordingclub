package in.recordingclub.admin.m_profiles_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import in.recordingclub.databinding.MProfilesManagerActivityBinding;

public class M_Profiles_Manager_Activity extends AppCompatActivity implements View.OnClickListener {

    private MProfilesManagerActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MProfilesManagerActivityBinding.inflate(getLayoutInflater());
        setTitle("Matrimonial Manager");
        setContentView(binding.getRoot());
        binding.mProfileCreate.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.mProfileCreate.getId()) {
            startActivity(new Intent(getApplicationContext(), Create_New_Profile.class));
        }
    }
}