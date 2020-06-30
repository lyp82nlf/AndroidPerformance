package com.dsg.androidperformance.memory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dsg.androidperformance.R;
import com.facebook.drawee.gestures.GestureDetector;

/**
 * @author DSG
 * @Project AndroidPerformance
 * @date 2020/6/30
 * @describe 模拟内存抖动
 */
public class MemorySharkActivity extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            for (int i = 0; i < 100; i++) {
                String args[] = new String[100000];
            }
            mHandler.sendEmptyMessageDelayed(0, 30);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        findViewById(R.id.bt_memory).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
