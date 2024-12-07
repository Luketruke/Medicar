package com.tp_integrador_p2;

import android.content.Intent;

import com.tp_integrador_p2.activity.launcherActivity;
import com.tp_integrador_p2.activity.loginActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MainActivity.this, launcherActivity.class);
        startActivity(intent);
        finish();
    }
}
