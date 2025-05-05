package in.recordingclub.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import in.recordingclub.databinding.DetailsManagerActivityBinding;

public class DetailsManager extends AppCompatActivity implements View.OnClickListener {

    private DetailsManagerActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DetailsManagerActivityBinding.inflate(getLayoutInflater());
        setTitle("User Informations");
        setContentView(binding.getRoot());
        binding.dmSeaallusers.setOnClickListener(this::onClick);
        binding.dmSeaallsubscribers.setOnClickListener(this::onClick);
        binding.dmGetFreeUsers.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == binding.dmSeaallusers.getId()) {
            startActivity(new Intent(getApplicationContext(), GetAllUsers.class));
        }
        if (id == binding.dmSeaallsubscribers.getId()) {
            startActivity(new Intent(getApplicationContext(), GetAllSubscribers.class));
        } else if (id == binding.dmGetFreeUsers.getId()) {
            startActivity(new Intent(getApplicationContext(), GetAllFreeUsers.class));
        }
    }
}