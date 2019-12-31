package radient.brain.taskmanagementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


//        Thread splashThread = new Thread(){
//            @Override
//            public void run() {
//                try {
//                    sleep(3000);
//                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        splashThread.start();

        Handler hadler=new Handler();
        hadler.postDelayed(new Runnable() {
            @Override
            public void run () {
                finish();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }, 3000);


    }
}
