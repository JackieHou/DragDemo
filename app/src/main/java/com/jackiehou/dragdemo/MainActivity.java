package com.jackiehou.dragdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.jackiehou.dragdemo.manager.FloatWindowManager;

import com.annimon.stream.Stream;

public class MainActivity extends BaseActivity {


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        MyApp.getInstance().setWindowManager(windowManager);

        /*new Handler().postDelayed(()->{
            MainActivity.this.finish();
        },2000);*/

        Stream.of(android.R.id.button1, android.R.id.button2,android.R.id.button3)
                .map(id -> findViewById(id))
                .forEach(v -> v.setOnClickListener(view -> {
                    switch (view.getId()) {
                        case android.R.id.button1:
                            view.postDelayed(() -> {
                                Intent intent = new Intent(MainActivity.this, DragActivity.class);
                                MainActivity.this.startActivity(intent);
                            }, 21);
                            break;
                        case android.R.id.button2:
                            FloatWindowManager.getInstance().showNaviBar(true);
                            break;
                        case android.R.id.button3:
                            Toast.makeText(MainActivity.this,"bottom click",Toast.LENGTH_LONG).show();
                            /*Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                            MainActivity.this.startActivity(intent);*/
                            break;
                        default:
                            break;

                    }
                }));


    }


}
