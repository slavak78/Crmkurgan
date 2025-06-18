package ru.crmkurgan.main.Activity;

import static ru.crmkurgan.main.Utils.Tools.setSystemBarColor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ru.crmkurgan.main.Constants.BaseApp;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Constants.VersionChecker;
import ru.crmkurgan.main.Fragment.ChatFragment;
import ru.crmkurgan.main.Fragment.HomeFragment;
import ru.crmkurgan.main.Fragment.MessageFragment;
import ru.crmkurgan.main.Fragment.PropertyFragment;
import ru.crmkurgan.main.Models.AboutModels;
import ru.crmkurgan.main.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import ru.crmkurgan.main.Fragment.FavouriteFragment;
import ru.crmkurgan.main.Fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    long mBackPressed;


    public static SharedPreferences sharedPreferences;
    public static String user_id;
    public static String user_name;
    public static String image;
    public static String image1;
    public static String token;
    public static String agent;
    public static String tel_agent;
    BaseApp baseApp;
    LinearLayout llsearch;
    DatabaseReference rootref;
    AboutModels modelAbout;
    public static String title="none";

    EditText search;

    public static MainActivity mainActivity;
    private FragmentManager fragmentManager;
    BottomNavigationView navigation;
    int previousSelect = 0;
    View drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSystemBarColor(this);
        fragmentManager = getSupportFragmentManager();
        llsearch = findViewById(R.id.llsearch);
        baseApp = BaseApp.getInstance();
        navigation = findViewById(R.id.navigation);
        drawerLayout = findViewById(R.id.MainFragment);

        navigation.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.home) {
                HomeFragment homeFragment = new HomeFragment();
                navigationItemSelected(0);
                loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                llsearch.setVisibility(View.VISIBLE);
            }
            if(item.getItemId()==R.id.property) {
                PropertyFragment propertyFragment = new PropertyFragment();
                navigationItemSelected(1);
                loadFrag(propertyFragment, getString(R.string.menu_property), fragmentManager);
                llsearch.setVisibility(View.GONE);
            }
            if(item.getItemId()==R.id.favourite) {
                FavouriteFragment matchFragment = new FavouriteFragment();
                navigationItemSelected(2);
                loadFrag(matchFragment, getString(R.string.menu_favourite), fragmentManager);
                llsearch.setVisibility(View.GONE);
            }
            if(item.getItemId()==R.id.chat) {
                MessageFragment messageFragment = new MessageFragment();
                navigationItemSelected(3);
                loadFrag(messageFragment, getString(R.string.chat), fragmentManager);
                llsearch.setVisibility(View.GONE);
            }
            if(item.getItemId()==R.id.user) {
                ProfileFragment profileFragment = new ProfileFragment();
                navigationItemSelected(4);
                loadFrag(profileFragment, getString(R.string.menu_profile), fragmentManager);
                llsearch.setVisibility(View.GONE);
            }
            return true;
        });
        modelAbout = new AboutModels();
        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
        mainActivity =this;
        sharedPreferences = getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        user_id = sharedPreferences.getString(Constants.uid, "null");
        user_name = sharedPreferences.getString(Constants.f_name, "");
        image =sharedPreferences.getString(Constants.u_pic,"null");
        image1 =sharedPreferences.getString("image1","null");
        agent =sharedPreferences.getString("agent","0");
        tel_agent =sharedPreferences.getString("tel_agent","0");
        token=sharedPreferences.getString(Constants.device_token, String.valueOf(FirebaseMessaging.getInstance().getToken()));
        rootref= FirebaseDatabase.getInstance().getReference();

        search = findViewById(R.id.search);

        search.setOnEditorActionListener((v, actionId, event) -> {
            String sSearch= search.getText().toString().trim();
            if (TextUtils.isEmpty(sSearch)) {

                Toast.makeText(MainActivity.this, getResources().getString(R.string.empt), Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("searchtext", sSearch);
                startActivity(intent);
                return true;
            }

            return false;
        });

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        Constants.versionname=packageInfo.versionName;

        Intent notifyIntent = getIntent();
        Bundle extras = notifyIntent.getExtras();
        if(extras!=null) {
            String key = extras.getString("KEY");
            if (key.equals("457927")) {
                chatFragment(extras.getString("senderid"), extras.getString("receiverid"), extras.getString("name"), extras.getString("pic"));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (baseApp.getIsLogin()) {
            rootref.child("Users").child(user_id).child("token").setValue(token);
        } else {
            rootref.child("Users").child(user_id).child("token").setValue("null");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Check_version();

    }

    @Override
    public void onBackPressed() {
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            if (mBackPressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.Container);
                if(f instanceof ProfileFragment) {
                    ((ProfileFragment) f).doreload();
                }
            } else {
                clickDone();

            }
        } else {
            super.onBackPressed();
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.Container);
            if(f instanceof ProfileFragment) {
                ((ProfileFragment) f).doreload();
            }
        }
    }

    public void clickDone() {
        new AlertDialog.Builder(this, R.style.AlertDialog)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.question))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }



    public void Check_version(){
        VersionChecker versionChecker = new VersionChecker();
        versionChecker.check(this, drawerLayout);
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
    }

    public void navigationItemSelected(int position) {
        previousSelect = position;
    }


    public void chatFragment(String senderid, String receiverid, String name, String pic){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = MainActivity.mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",pic);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();
    }

}
