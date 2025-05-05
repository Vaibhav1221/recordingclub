package in.recordingclub.admin.newspaper_manager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.videvelopers.app.vuh.AppInitializer;

import in.recordingclub.R;
import in.recordingclub.databinding.NewspaperManagerActivityBinding;

public class NewspaperManagerActivity extends AppCompatActivity {

    private NewspaperManagerActivityBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), NewspaperManagerActivity.this);
        binding = NewspaperManagerActivityBinding.inflate(getLayoutInflater());
        setTitle("Newspaper Manager");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("My Account. Back");
        NewspaperManagerAdapter newspaperManagerAdapter = new NewspaperManagerAdapter(NewspaperManagerActivity.this, getResources().getStringArray(R.array.newspaper_manager_features));
        binding.newspaperManagerLv.setAdapter(newspaperManagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
