package com.example.parallel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void mvc(View v) {
        Intent intent = new Intent(this,MvcActivity.class);
        startActivity(intent);
    }
    public void mvp(View v) {
        Intent intent = new Intent(this,MvpActivity.class);
        startActivity(intent);
    }
    public void test(View v) {
        Intent intent = new Intent(this,TestActivity.class);
        startActivity(intent);
    }

}
