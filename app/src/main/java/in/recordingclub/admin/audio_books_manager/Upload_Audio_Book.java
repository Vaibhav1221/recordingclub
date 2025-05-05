package in.recordingclub.admin.audio_books_manager;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.videvelopers.app.vuh.AppInitializer;

import in.recordingclub.R;
import in.recordingclub.databinding.UploadAudioBookBinding;

public class Upload_Audio_Book extends AppCompatActivity {

    private UploadAudioBookBinding binding;
    private WebView webView;
    private AppInitializer initializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UploadAudioBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializer = new AppInitializer(getApplicationContext(), Upload_Audio_Book.this);
        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(initializer.getURLHelpers().webView);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}