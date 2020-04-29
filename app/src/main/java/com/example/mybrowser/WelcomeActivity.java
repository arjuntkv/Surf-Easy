package com.example.mybrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window g=getWindow();
        g.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.TYPE_STATUS_BAR);
        setContentView(R.layout.activity_welcome);

        Thread th=new Thread()
        {
          @Override
          public void run(){
              try{
                  sleep(3000);
              }
              catch (Exception e){
                  e.printStackTrace();
              }
              finally {
                  startActivity(new Intent(getApplicationContext(), MainActivity.class));
              }
          }
        };
        th.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }
}
