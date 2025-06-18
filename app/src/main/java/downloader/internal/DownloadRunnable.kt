package downloader.internal

import downloader.Error
import downloader.Priority
import downloader.Status
import downloader.request.DownloadRequest

class DownloadRunnable internal constructor(val request: DownloadRequest) : Runnable {
    val priority: Priority
    val sequence: Int
    override fun run() {
        val request1 = request
        request1.status = Status.RUNNING
        val downloadTask = DownloadTask.create(request)
        val response = downloadTask.run()
        if (response.isSuccessful) {
            request1.deliverSuccess()
        } else if (response.isPaused) {
            request1.deliverPauseEvent()
        } else if (response.error != null) {
            request1.deliverError(response.error)
        } else if (!response.isCancelled) {
            request1.deliverError(Error())
        }
    }

    init {
        val request1 = request
        priority = request1.priority
        sequence = request1.sequenceNumber
    }
}