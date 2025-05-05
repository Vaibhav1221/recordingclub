package in.recordingclub.admin.audio_books_manager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.videvelopers.app.vuh.AppInitializer;

import in.recordingclub.R;
import in.recordingclub.databinding.AudioBooksManagerActivityBinding;

public class AudioBooksManagerActivity extends AppCompatActivity {

    private AudioBooksManagerActivityBinding binding;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer = new AppInitializer(getApplicationContext(), AudioBooksManagerActivity.this);
        binding = AudioBooksManagerActivityBinding.inflate(getLayoutInflater());
        setTitle("Audio Books Manager");
        setContentView(binding.getRoot());
        initializer.getAppActionBar().setCustomActionBarWithBackButton("My Account. Back");
        setInit();
    }

    private void setInit() {
        AudioBooksManagerAdapter audioBooksManagerAdapter = new AudioBooksManagerAdapter(AudioBooksManagerActivity.this, getResources().getStringArray(R.array.audio_books_manager_features));
        binding.audioBooksManagerLvFeatures.setAdapter(audioBooksManagerAdapter);
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
