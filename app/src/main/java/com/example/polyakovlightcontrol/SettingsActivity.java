package com.example.polyakovlightcontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText etIpAddress = findViewById(R.id.etIpAddress);
        Button btnSave = findViewById(R.id.btnSave);

        // Загрузка текущего IP при открытии окна
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String savedIp = prefs.getString("ip_address", "192.168.4.1");
        etIpAddress.setText(savedIp);

        // Сохранение нового IP и возврат назад
        btnSave.setOnClickListener(v -> {
            String newIp = etIpAddress.getText().toString().trim();

            // Строгое регулярное выражение для проверки IPv4
            String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

            if (newIp.matches(ipPattern)) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ip_address", newIp);
                editor.apply();
                finish(); // Закрывает окно настроек
            } else {
                Toast.makeText(SettingsActivity.this, "Введите корректный IP-адрес!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}