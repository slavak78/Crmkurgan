package ru.crmkurgan.main.Utils;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import ru.crmkurgan.main.BuildConfig;

public final class UpdateManager {
    public interface UpdateListener {
        void onShowSnackbar();
    }

    private UpdateListener updateListener;
    private static UpdateManager instance;
    private AppUpdateManager appUpdateManager;
    public final static int UPDATE_REQUEST_CODE = 8;
    private final String ERROR_TAG = "UPDATE_ERROR";

    private UpdateManager() {
    }

    public static UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }

        return instance;
    }

    public void attachUpdateListener(UpdateListener listener) {
        updateListener = listener;
    }

    public void detachUpdateListener() {
        updateListener = null;
    }

    public void checkForUpdate(Activity activity) {
        appUpdateManager = AppUpdateManagerFactory.create(activity);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                if (updateListener != null) {
                    updateListener.onShowSnackbar();
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                if (!activity.isFinishing()) {
                    try {
                        startUpdate(appUpdateInfo, activity);
                    } catch (IntentSender.SendIntentException e) {
                        if (BuildConfig.DEBUG) {
                            Log.e(ERROR_TAG, "SendError: " + e);
                        }
                    }
                }
            }
        });
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo, Activity activity)
            throws IntentSender.SendIntentException {
        if (appUpdateManager == null) return;

        appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                UPDATE_REQUEST_CODE);
    }

    public void registerListener() {
        if (appUpdateManager == null) return;

        appUpdateManager.registerListener(listener);
    }

    public void completeUpdate() {
        if (appUpdateManager == null) return;

        appUpdateManager.completeUpdate();
    }

    private final InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            onUpdateDownloaded();
        }
    };

    private void onUpdateDownloaded() {
        if (appUpdateManager == null) return;

        appUpdateManager.unregisterListener(listener);
        if (updateListener != null) {
            updateListener.onShowSnackbar();
        } else {
            appUpdateManager.completeUpdate();
        }
    }
}