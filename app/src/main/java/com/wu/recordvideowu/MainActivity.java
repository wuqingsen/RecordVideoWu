package com.wu.recordvideowu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wu.recordvideowu.utils.FaceView;
import com.wu.recordvideowu.utils.PermissionsChecker;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.button)
    Button button;

    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private final int RESULT_CODE_LOCATION = 0x001;

    //定位权限,获取app内常用权限
    String[] permsLocation = {"android.permission.READ_PHONE_STATE"
            , "android.permission.READ_EXTERNAL_STORAGE"
            , "android.permission.CAMERA"
            , "android.permission.RECORD_AUDIO"
            , "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecordVideoRealActivity.class));
            }
        });

        mPermissionsChecker = new PermissionsChecker(MainActivity.this);
        if (mPermissionsChecker.lacksPermissions(permsLocation)) {
            ActivityCompat.requestPermissions(MainActivity.this, permsLocation, RESULT_CODE_LOCATION);
        }

    }
}
