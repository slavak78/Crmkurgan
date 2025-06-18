package ru.crmkurgan.main.Activity;

import static ru.crmkurgan.main.Utils.Tools.setSystemAndNavigationBarColor;
import static ru.crmkurgan.main.Utils.Tools.setSystemBarColor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Constants.Constants;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
     //   this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        setSystemAndNavigationBarColor(this);
        sharedPreferences=getSharedPreferences(Constants.pref_name,MODE_PRIVATE);
            new Handler().postDelayed(() -> {
                    if(getIntent().hasExtra("action_type")){
                        Intent intent= new Intent(SplashActivity.this, MainActivity.class);
                        String action_type=getIntent().getExtras().getString("action_type");
                        String receiverid=getIntent().getExtras().getString("senderid");
                        String title=getIntent().getExtras().getString("title");
                        String icon=getIntent().getExtras().getString("icon");

                        intent.putExtra("icon",icon);
                        intent.putExtra("action_type",action_type);
                        intent.putExtra("receiverid",receiverid);
                        intent.putExtra("title",title);


                        startActivity(intent);
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        finish();
                    }
            }, 2000);



    }
}
