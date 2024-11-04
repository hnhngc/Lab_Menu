package ngochuynh.ueh.edu.lab_menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private RadioGroup rgLanguage, rgTheme;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;
    private boolean languageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale(); // Load current locale before setting content view
        setContentView(R.layout.activity_setting);

        // Initialize views
        rgLanguage = findViewById(R.id.rgLanguage);
        rgTheme = findViewById(R.id.rgTheme);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);

        // Load saved settings
        loadSavedSettings();

        // Set up language change listener
        rgLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            boolean newIsEnglish = checkedId == R.id.rbEnglish;
            boolean currentIsEnglish = sharedPreferences.getBoolean("isEnglish", true);
            if (newIsEnglish != currentIsEnglish) {
                languageChanged = true;
            }
        });

        // Save button click listener
        btnSaveSettings.setOnClickListener(v -> saveSettings());
    }

    private void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isEnglish = prefs.getBoolean("isEnglish", true);
        String languageCode = isEnglish ? "en" : "vi";

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private void loadSavedSettings() {
        // Load language setting
        boolean isEnglish = sharedPreferences.getBoolean("isEnglish", true);
        rgLanguage.check(isEnglish ? R.id.rbEnglish : R.id.rbVietnamese);

        // Load theme setting
        boolean isColorful = sharedPreferences.getBoolean("isColorful", true);
        rgTheme.check(isColorful ? R.id.rbColorful : R.id.rbDark);

        // Update button text based on current language
        updateButtonText();
    }

    private void updateButtonText() {
        boolean isEnglish = sharedPreferences.getBoolean("isEnglish", true);
        btnSaveSettings.setText(isEnglish ? "Save Settings" : "Lưu Cài Đặt");
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

        // Show success message in appropriate language
        String message = isEnglish ? "Settings saved" : "Đã lưu cài đặt";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        if (languageChanged) {
            // Restart the main activity to apply language changes
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            // Just finish this activity if only theme was changed
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale(); // Ensure correct locale is applied
        updateButtonText(); // Update UI text based on current language
    }
}