package com.example.polyakovlightcontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private String currentIp;
    private TextView tvStatus;
    private TextView tvIpLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvIpLabel = findViewById(R.id.tvIpLabel);
        Button btnOn = findViewById(R.id.btnOn);
        Button btnOff = findViewById(R.id.btnOff);
        Button btnSettings = findViewById(R.id.btnSettings);
        SeekBar seekBarBrightness = findViewById(R.id.seekBarBrightness);

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        btnOn.setOnClickListener(v -> {
            sendCommand("/led_on");
            seekBarBrightness.setProgress(255);
        });

        btnOff.setOnClickListener(v -> {
            sendCommand("/led_off");
            seekBarBrightness.setProgress(0);
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendCommand("/brightness?val=" + seekBar.getProgress());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Загрузка сохраненного IP при каждом возврате на главный экран
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentIp = prefs.getString("ip_address", "192.168.4.1");
        tvIpLabel.setText("IP платы: " + currentIp);

        // Проверка связи при запуске
        sendCommand("/status");
    }

    private void sendCommand(String endpoint) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL("http://" + currentIp + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(2000);

                int responseCode = connection.getResponseCode();

                // Если запрос прошел успешно, меняем статус в главном потоке
                if (responseCode == 200) {
                    runOnUiThread(() -> {
                        tvStatus.setText("Статус: Подключено");
                        tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    });
                }
                connection.disconnect();
            } catch (Exception e) {
                // Если плата недоступна, выводим ошибку
                runOnUiThread(() -> {
                    tvStatus.setText("Статус: Ошибка подключения");
                    tvStatus.setTextColor(Color.parseColor("#F44336"));
                });
                e.printStackTrace();
            }
        });
    }
}