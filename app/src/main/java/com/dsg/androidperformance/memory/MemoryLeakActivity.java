package com.dsg.androidperformance.memory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dsg.androidperformance.R;

/**
 * @author DSG
 * @Project AndroidPerformance
 * @date 2020/6/30
 * @describe
 */
public class MemoryLeakActivity extends AppCompatActivity implements CallBack {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoryleak);
        ImageView imageView = findViewById(R.id.iv_memoryleak);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.splash);
        imageView.setImageBitmap(bitmap);

        CallBackManager.addCallBack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        CallBackManager.removeCallBack(this);
    }

    @Override
    public void dpOperate() {
        // do sth
    }


}
