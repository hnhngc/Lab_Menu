package ngochuynh.ueh.edu.lab_menu;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private static final int TOTAL_BUTTONS = 16;
    private static final int MAX_NUMBER = 100;
    private Button btnStart;
    private TextView tvTimer;
    private Handler timerHandler;
    private long startTime = 0L;
    private boolean isGameRunning = false;

    // Định nghĩa mảng màu nền và màu chữ riêng biệt
    private static final int[] BACKGROUND_COLORS = {
            Color.rgb(255, 182, 193), // Light pink
            Color.rgb(173, 216, 230), // Light blue
            Color.rgb(144, 238, 144), // Light green
            Color.rgb(255, 218, 185), // Peach
            Color.rgb(221, 160, 221), // Plum
            Color.rgb(176, 224, 230), // Powder blue
            Color.rgb(255, 255, 224), // Light yellow
            Color.rgb(230, 230, 250)  // Lavender
    };

    private static final int[] TEXT_COLORS = {
            Color.rgb(139, 0, 0),     // Dark red
            Color.rgb(0, 0, 139),     // Dark blue
            Color.rgb(0, 100, 0),     // Dark green
            Color.rgb(139, 69, 19),   // Saddle brown
            Color.rgb(75, 0, 130),    // Indigo
            Color.rgb(25, 25, 112),   // Midnight blue
            Color.rgb(128, 0, 0),     // Maroon
            Color.rgb(85, 107, 47)    // Dark olive green
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        setSupportActionBar((MaterialToolbar) findViewById(R.id.txtToolBar));

        timerHandler = new Handler();
        btnStart = findViewById(R.id.btnStart);
        tvTimer = findViewById(R.id.tvTimer);


        // Disable tất cả buttons lúc khởi động
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
            // Chuyển sang SettingActivity
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.mni_favorite) {
            // Xử lý khi click vào Favorite
            Toast.makeText(this, "Favorite clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeGame() {
        numbers = generateRandomNumbers();
        currentSmallestNumber = findSmallestNumber();

        // Reset tất cả buttons về trạng thái ban đầu
        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setEnabled(true);
            button.setElevation(8f);
            button.setText(String.valueOf(numbers.get(i)));
        }

        // Cập nhật màu cho các button
        updateButtonColors();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isGameRunning) {
            startTime = System.currentTimeMillis() - (getMinutesFromTimer() * 60 + getSecondsFromTimer()) * 1000;
            startTimer();
        }
        updateButtonColors();
    }

    private int getMinutesFromTimer() {
        String time = tvTimer.getText().toString();
        return Integer.parseInt(time.split(":")[0]);
    }

    private int getSecondsFromTimer() {
        String time = tvTimer.getText().toString();
        return Integer.parseInt(time.split(":")[1]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGameRunning) {
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

            // Chỉ cập nhật màu cho các button còn active
            if (button.isEnabled()) {
                if (isColorful) {
                    // Sử dụng màu random
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
                    // Sử dụng theme đen
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
        Random random = new Random();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isColorful = sharedPreferences.getBoolean("isColorful", true);

        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            int buttonId = getResources().getIdentifier("btn" + i, "id", getPackageName());
            Button button = findViewById(buttonId);

            if (isColorful) {
                // Sử dụng màu random như cũ
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
                // Sử dụng theme đen
                button.setBackgroundColor(Color.BLACK);
                button.setTextColor(Color.WHITE);
            }

            button.setText(String.valueOf(numbers.get(i)));
            button.setEnabled(true);
            button.setElevation(8f);
            button.setOnClickListener(v -> handleButtonClick(button));
        }
    }

    private void handleButtonClick(Button button) {
        int selectedNumber = Integer.parseInt(button.getText().toString());
        if (currentSmallestNumber > 0 && selectedNumber == currentSmallestNumber) { // Kiểm tra số hợp lệ
            button.setBackgroundColor(Color.GREEN);
            button.setTextColor(Color.WHITE);
            button.setEnabled(false);
            button.setElevation(0f);

            // Cập nhật số nhỏ nhất mới
            numbers.remove(Integer.valueOf(currentSmallestNumber));
            if (!numbers.isEmpty()) {
                currentSmallestNumber = findSmallestNumber();
            } else {
                showWinDialog();
            }
        } else {
            // Sai số
            showErrorAnimation(button);
            Toast.makeText(this, "Hãy tìm số nhỏ nhất!!!", Toast.LENGTH_SHORT).show();
        }
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

    // Thêm phương thức xử lý Start game
    private void startGame() {
        if (!isGameRunning) {
            isGameRunning = true;
            btnStart.setText("Reset");
            initializeGame();
            enableAllButtons();
            assignNumbersToButtons(); // Gọi phương thức để gán số cho các nút
            startTimer();
        } else {
            // Reset game
            isGameRunning = false;
            btnStart.setText("Start");
            stopTimer();
            tvTimer.setText("00:00");
            disableAllButtons();
        }
    }

    // Thêm các phương thức xử lý timer
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
        stopTimer(); // Dừng timer khi thắng
        isGameRunning = false;
        btnStart.setText("Start");

        new AlertDialog.Builder(this)
                .setTitle("Chúc mừng!")
                .setMessage("Bạn đã hoàn thành trò chơi trong " + tvTimer.getText() + ". Bạn muốn chơi lại không?")
                .setPositiveButton("Chơi lại", (dialog, which) -> {
                    startGame();
                })
                .setNegativeButton("Thoát", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}