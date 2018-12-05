package com.example.dellpc.bus_tracker;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.example.dellpc.bus_tracker.R;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        try {
            PackageInfo pInfo = null;
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            TextView textViewVersion = (TextView)findViewById(R.id.textViewVersion);
            String version = pInfo.versionName;
            textViewVersion.setText(getString(R.string.version) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    //Verify login information
                    /*SharedPreferences pref;
                    pref = getApplicationContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

                    String storedText = pref.getString("email", null);
                    if(storedText==null)
                    {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(intent);

                    }
                    else if(storedText.trim().isEmpty()==false) {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    }*/

                    //No validation required
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
