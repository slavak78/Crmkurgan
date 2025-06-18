package imagepicker.ui.camera

import android.content.Context
import android.content.Intent
import imagepicker.model.Config

interface CameraModule {
    fun getCameraIntent(context: Context, config: Config): Intent?
    fun getImage(context: Context, isRequireId: Boolean, imageReadyListener: OnImageReadyListener?)
}