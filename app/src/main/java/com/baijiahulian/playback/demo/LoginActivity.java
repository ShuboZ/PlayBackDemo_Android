package com.baijiahulian.playback.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        findViewById(R.id.startPlayBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.classId);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("classId", Long.parseLong(editText.getText().toString()));
                startActivity(intent);
            }
        });
    }
}
