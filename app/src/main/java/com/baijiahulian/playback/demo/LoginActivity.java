package com.baijiahulian.playback.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observable;
import rx.functions.Action1;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        findViewById(R.id.startPlayBack).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Observable<Boolean> observable = new RxPermissions(LoginActivity.this)
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);

                observable.subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            EditText editText = (EditText) findViewById(R.id.classId);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("classId", Long.parseLong(editText.getText().toString()));
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "无法操作", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        findViewById(R.id.offlinePlacback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Observable<Boolean> observable = new RxPermissions(LoginActivity.this)
                        .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE);

                observable.subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            EditText editText = (EditText) findViewById(R.id.classId);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            String classId = editText.getText().toString();
                            intent.putExtra("classId", Long.parseLong(classId));
                            intent.putExtra("offline", true);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "无法操作", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
