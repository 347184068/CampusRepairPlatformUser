package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import wfu.com.campusrepairplatformUser.R;

/**
 * Created by Lenovo on 2016/9/18.
 */
public class SplashActivity extends Activity {
    private int SplashTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            // 为了减少代码使用匿名Handler创建一个延时的调用
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);//通过Intent打开最终真正的主界面Main这个Activity
                SplashActivity.this.startActivity(i);    //启动Main界面
                SplashActivity.this.finish();    //关闭自己这个开场屏
            }


        }, SplashTime);
    }

}