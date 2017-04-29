package iqubal.ashraf.videoken;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by ashrafiqubal on 28/04/17.
 */

public class SplashScreen extends AppCompatActivity {
    final int SPLASH_TIME_OUT = 3000; // time in milliseconds
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
            proceedNormally();
            }
        }, SPLASH_TIME_OUT);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    private void proceedNormally(){
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
