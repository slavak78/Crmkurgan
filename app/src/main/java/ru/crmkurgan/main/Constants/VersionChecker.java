package ru.crmkurgan.main.Constants;

import android.app.Activity;
import android.content.IntentSender;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import ru.crmkurgan.main.R;

public class VersionChecker {

    public void check(Activity context, View view1) {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(context);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            context,
                            8
                    );
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }

            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                Snackbar.make(view1, context.getResources().getString(R.string.downloaded),Snackbar.LENGTH_INDEFINITE).setAction(context.getResources().getString(R.string.reload), view -> appUpdateManager.completeUpdate()).show();
            }
        });
    }
}