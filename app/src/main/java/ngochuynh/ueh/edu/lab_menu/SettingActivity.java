package ngochuynh.ueh.edu.lab_menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class SettingActivity extends AppCompatActivity {
    private RadioGroup rgLanguage, rgTheme;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize views
        rgLanguage = findViewById(R.id.rgLanguage);
        rgTheme = findViewById(R.id.rgTheme);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Load saved settings
        loadSavedSettings();

        // Save button click listener
        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void loadSavedSettings() {
        // Load language setting
        boolean isEnglish = sharedPreferences.getBoolean("isEnglish", true);
        rgLanguage.check(isEnglish ? R.id.rbEnglish : R.id.rbVietnamese);

        // Load theme setting
        boolean isColorful = sharedPreferences.getBoolean("isColorful", true);
        rgTheme.check(isColorful ? R.id.rbColorful : R.id.rbDark);
    }


    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save language setting
        boolean isEnglish = rgLanguage.getCheckedRadioButtonId() == R.id.rbEnglish;
        editor.putBoolean("isEnglish", isEnglish);

        // Save theme setting
        boolean isColorful = rgTheme.getCheckedRadioButtonId() == R.id.rbColorful;
        editor.putBoolean("isColorful", isColorful);

        editor.apply();

        // Show success message
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();

        // Kết thúc activity để quay về MainActivity
        finish();
    }
}