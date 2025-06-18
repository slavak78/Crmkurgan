package downloader.handler

import android.os.Handler
import android.os.Looper
import android.os.Message
import downloader.Constants
import downloader.OnProgressListener
import downloader.Progress

class ProgressHandler(private val listener: OnProgressListener?) : Handler(Looper.getMainLooper()) {
    override fun handleMessage(msg: Message) {
        if (msg.what == Constants.UPDATE) {
            listener.let {
                val progress = msg.obj as Progress
                it?.onProgress(progress)
            }
        } else {
            super.handleMessage(msg)
        }
    }
}