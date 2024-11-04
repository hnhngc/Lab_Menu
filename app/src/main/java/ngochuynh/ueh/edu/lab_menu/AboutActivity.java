package ngochuynh.ueh.edu.lab_menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {
    private TextView tvHowToPlayTitle, tvObjectiveTitle, tvObjectiveContent,
            tvRulesTitle, tvRulesContent;
    private Button btnStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_about);

        // Initialize views
        initializeViews();

        // Update text content based on language
        updateContent();

        // Set click listener for start game button
        btnStartGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        tvHowToPlayTitle = findViewById(R.id.tvHowToPlayTitle);
        tvObjectiveTitle = findViewById(R.id.tvObjectiveTitle);
        tvObjectiveContent = findViewById(R.id.tvObjectiveContent);
        tvRulesTitle = findViewById(R.id.tvRulesTitle);
        tvRulesContent = findViewById(R.id.tvRulesContent);
        btnStartGame = findViewById(R.id.btnStartGame);
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

    private void updateContent() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isEnglish = prefs.getBoolean("isEnglish", true);

        if (isEnglish) {
            setEnglishContent();
        } else {
            setVietnameseContent();
        }
    }

    private void setEnglishContent() {
        tvHowToPlayTitle.setText("How To Play");
        tvObjectiveTitle.setText("Game Objective");
        tvObjectiveContent.setText("Find and click on the smallest number among the displayed numbers.");
        tvRulesTitle.setText("Game Rules");
        tvRulesContent.setText(
                "1. Click 'Start' to begin the game\n" +
                        "2. Find and click the smallest number\n" +
                        "3. You have 3 attempts before game over\n" +
                        "4. Timer tracks your completion time\n" +
                        "5. Complete all numbers to win"
        );
        btnStartGame.setText("Back");
    }

    private void setVietnameseContent() {
        tvHowToPlayTitle.setText("Cách Chơi");
        tvObjectiveTitle.setText("Mục Tiêu");
        tvObjectiveContent.setText("Tìm và nhấp vào số nhỏ nhất trong các số hiển thị.");
        tvRulesTitle.setText("Luật Chơi");
        tvRulesContent.setText(
                "1. Nhấn 'Bắt Đầu' để bắt đầu trò chơi\n" +
                        "2. Tìm và nhấp vào số nhỏ nhất\n" +
                        "3. Bạn có 3 lần thử trước khi thua\n" +
                        "4. Đồng hồ sẽ theo dõi thời gian hoàn thành\n" +
                        "5. Hoàn thành tất cả các số để chiến thắng"
        );
        btnStartGame.setText("Quay lại");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
        updateContent();
    }
}