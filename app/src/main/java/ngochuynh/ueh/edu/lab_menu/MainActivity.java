package ngochuynh.ueh.edu.lab_menu;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private List<Integer> numbers;
    private int currentSmallestNumber;
    private static final int TOTAL_BUTTONS = 4;
    private static final int MAX_NUMBER = 100;
    private Button btnStart;
    private TextView tvTimer;
    private Handler timerHandler;
    private long startTime = 0L;
    private boolean isGameRunning = false;
    private int wrongAttempts = 0;
    private static final int MAX_WRONG_ATTEMPTS = 3;
    private long pausedTime = 0L;

    private static final int[] BACKGROUND_COLORS = {
            Color.rgb(255, 182, 193),
            Color.rgb(173, 216, 230),
            Color.rgb(144, 238, 144),
            Color.rgb(255, 218, 185),
            Color.rgb(221, 160, 221),
            Color.rgb(176, 224, 230),
            Color.rgb(255, 255, 224),
            Color.rgb(230, 230, 250)
    };

    private static final int[] TEXT_COLORS = {
            Color.rgb(139, 0, 0),
            Color.rgb(0, 0, 139),
            Color.rgb(0, 100, 0),
            Color.rgb(139, 69, 19),
            Color.rgb(75, 0, 130),
            Color.rgb(25, 25, 112),
            Color.rgb(128, 0, 0),
            Color.rgb(85, 107, 47)
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        setSupportActionBar((MaterialToolbar) findViewById(R.id.txtToolBar));

        timerHandler = new Handler();
        btnStart = findViewById(R.id.btnStart);
        tvTimer = findViewById(R.id.tvTimer);

        disableAllButtons();
        btnStart.setOnClickListener(v -> startGame());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mni_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        }
        else {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
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

    private void initializeGame() {
        numbers = generateRandomNumbers();
        currentSmallestNumber = findSmallestNumber();
        wrongAttempts = 0;

        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setEnabled(true);
            button.setElevation(8f);
            button.setText(String.valueOf(numbers.get(i)));
        }

        updateButtonColors();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
        if (isGameRunning && pausedTime > 0) {
            startTime = pausedTime;
            startTimer();
        }
        updateButtonColors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGameRunning) {
            pausedTime = startTime;
            stopTimer();
        }
    }

    private void updateButtonColors() {
        Random random = new Random();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isColorful = sharedPreferences.getBoolean("isColorful", true);

        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);

            if (button.isEnabled()) {
                if (isColorful) {
                    int backgroundColorIndex = random.nextInt(BACKGROUND_COLORS.length);
                    int backgroundColor = BACKGROUND_COLORS[backgroundColorIndex];
                    int textColorIndex;
                    do {
                        textColorIndex = random.nextInt(TEXT_COLORS.length);
                    } while (textColorIndex == backgroundColorIndex);
                    int textColor = TEXT_COLORS[textColorIndex];

                    button.setBackgroundColor(backgroundColor);
                    button.setTextColor(textColor);
                } else {
                    button.setBackgroundColor(Color.BLACK);
                    button.setTextColor(Color.WHITE);
                }
            }
        }
    }

    private List<Integer> generateRandomNumbers() {
        List<Integer> nums = new ArrayList<>();
        Random random = new Random();

        while (nums.size() < TOTAL_BUTTONS) {
            int num = random.nextInt(MAX_NUMBER) + 1;
            if (!nums.contains(num)) {
                nums.add(num);
            }
        }
        return nums;
    }

    private int findSmallestNumber() {
        return Collections.min(numbers);
    }

    private void assignNumbersToButtons() {
        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setText(String.valueOf(numbers.get(i)));
            button.setEnabled(true);
            button.setElevation(8f);
            button.setOnClickListener(v -> handleButtonClick(button));
        }
        updateButtonColors();
    }

    private void handleButtonClick(Button button) {
        int selectedNumber = Integer.parseInt(button.getText().toString());
        if (currentSmallestNumber > 0 && selectedNumber == currentSmallestNumber) {
            button.setBackgroundColor(Color.GREEN);
            button.setTextColor(Color.WHITE);
            button.setEnabled(false);
            button.setElevation(0f);
            wrongAttempts = 0;

            numbers.remove(Integer.valueOf(currentSmallestNumber));
            if (!numbers.isEmpty()) {
                currentSmallestNumber = findSmallestNumber();
            } else {
                showWinDialog();
            }
        } else {
            wrongAttempts++;
            showErrorAnimation(button);
            String message = isEnglish() ? "Find the smallest number!" : "Hãy tìm số nhỏ nhất!";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            if (wrongAttempts >= MAX_WRONG_ATTEMPTS) {
                showLoseDialog();
            }
        }
    }

    private boolean isEnglish() {
        return getSharedPreferences("Settings", MODE_PRIVATE)
                .getBoolean("isEnglish", true);
    }

    private void showErrorAnimation(Button button) {
        button.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() ->
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start())
                .start();
    }

    private void disableAllButtons() {
        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setEnabled(false);
        }
    }

    private void enableAllButtons() {
        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setEnabled(true);
        }
    }

    private void startGame() {
        if (!isGameRunning) {
            isGameRunning = true;
            btnStart.setText(isEnglish() ? "Reset" : "Làm Mới");
            initializeGame();
            enableAllButtons();
            assignNumbersToButtons();
            startTimer();
        } else {
            isGameRunning = false;
            btnStart.setText(isEnglish() ? "Start" : "Bắt Đầu");
            stopTimer();
            tvTimer.setText("00:00");
            disableAllButtons();
            pausedTime = 0L;
        }
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(updateTimerThread, 0);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(updateTimerThread);
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            long timeInMilliseconds = System.currentTimeMillis() - startTime;
            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            tvTimer.setText(String.format("%02d:%02d", mins, secs));
            timerHandler.postDelayed(this, 500);
        }
    };

    private void showWinDialog() {
        stopTimer();
        isGameRunning = false;
        btnStart.setText(isEnglish() ? "Start" : "Bắt Đầu");

        String title = isEnglish() ? "Congratulations!" : "Chúc mừng!";
        String message = isEnglish()
                ? "You completed the game in " + tvTimer.getText() + ". Would you like to play again?"
                : "Bạn đã hoàn thành trò chơi trong " + tvTimer.getText() + ". Bạn có muốn chơi lại không?";
        String playAgain = isEnglish() ? "Play Again" : "Chơi Lại";
        String exit = isEnglish() ? "Exit" : "Thoát";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(playAgain, (dialog, which) -> startGame())
                .setNegativeButton(exit, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void showLoseDialog() {
        stopTimer();
        isGameRunning = false;
        btnStart.setText(isEnglish() ? "Start" : "Bắt Đầu");

        String title = isEnglish() ? "Game Over" : "Kết Thúc";
        String message = isEnglish()
                ? "You made 3 wrong attempts. Game Over!"
                : "Bạn đã chọn sai 3 lần. Trò chơi kết thúc!";
        String playAgain = isEnglish() ? "Play Again" : "Chơi Lại";
        String exit = isEnglish() ? "Exit" : "Thoát";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(playAgain, (dialog, which) -> startGame())
                .setNegativeButton(exit, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}