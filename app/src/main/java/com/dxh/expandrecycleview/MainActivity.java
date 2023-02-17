package com.dxh.expandrecycleview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dxh.expandrecycleview.ui.NomalExpandActivity;
import com.dxh.expandrecycleview.ui.NotSameHeightExpandActivity;
import com.dxh.expandrecycleview.ui.PhoneExpandActivity;
import com.dxh.expandrecycleview.ui.SameHeightExpandActivity;


public class MainActivity extends AppCompatActivity {

    private Button btnOpenSameHeightExpandActivity;
    private Button btnOpenNotSameHeightExpandActivity;
    private Button btnOpenPhoneExpandActivity;
    private Button btnOpenNomalExpandActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        btnOpenNomalExpandActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NomalExpandActivity.class));
            }
        });
        btnOpenPhoneExpandActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhoneExpandActivity.class));
            }
        });
        btnOpenNotSameHeightExpandActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotSameHeightExpandActivity.class));
            }
        });
        btnOpenSameHeightExpandActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SameHeightExpandActivity.class));
            }
        });
    }

    private void initView() {
        btnOpenSameHeightExpandActivity = (Button) findViewById(R.id.btnOpenSameHeightExpandActivity);
        btnOpenNotSameHeightExpandActivity = (Button) findViewById(R.id.btnOpenNotSameHeightExpandActivity);
        btnOpenPhoneExpandActivity = (Button) findViewById(R.id.btnOpenPhoneExpandActivity);
        btnOpenNomalExpandActivity = (Button) findViewById(R.id.btnOpenNomalExpandActivity);
    }
}