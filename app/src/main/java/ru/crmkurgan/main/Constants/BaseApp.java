package ru.crmkurgan.main.Constants;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import dadata.realm.modules.QueryRealmModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BaseApp extends Application {
    private static BaseApp mInstance;
    public SharedPreferences preferences;
    public String prefName = "CRMKurgan";
    public BaseApp() {
        mInstance = this;
    }
    private static final String ONESIGNAL_APP_ID = "4abb18a1-4caf-4e2a-a84b-6205e76cc829";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .modules(new QueryRealmModule())
                .name("queries.realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized BaseApp getInstance() {
        return mInstance;
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedIn", false);
    }
}
