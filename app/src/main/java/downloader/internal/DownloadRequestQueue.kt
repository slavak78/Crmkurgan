package downloader.internal

import downloader.Status
import downloader.core.Core.Companion.getInstance
import downloader.request.DownloadRequest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class DownloadRequestQueue private constructor() {
    private val currentRequestMap: MutableMap<Int, DownloadRequest>
    private val sequenceGenerator: AtomicInteger
    private val sequenceNumber: Int
        get() = sequenceGenerator.incrementAndGet()

    fun pause(downloadId: Int) {
        val request = currentRequestMap[downloadId]
        request?.status = Status.PAUSED
    }

    fun resume(downloadId: Int) {
        val request = currentRequestMap[downloadId]
        request?.let {
            it.status = Status.QUEUED
            it.future = getInstance()
                ?.executorSupplier
                ?.forDownloadTasks()
                ?.submit(DownloadRunnable(request))
        }
    }

    private fun cancelAndRemoveFromMap(request: DownloadRequest?) {
        request?.let {
            it.cancel()
            currentRequestMap.remove(it.downloadId)
        }
    }

    fun cancel(downloadId: Int) {
        val request = currentRequestMap[downloadId]
        cancelAndRemoveFromMap(request)
    }

    fun cancel(tag: Any) {
        for ((_, request) in currentRequestMap) {
            val tag1 = request.tag
            if (tag1 is String && tag is String) {
                if (tag1 == tag) {
                    cancelAndRemoveFromMap(request)
                }
            } else if (tag1 == tag) {
                cancelAndRemoveFromMap(request)
            }
        }
    }

    fun cancelAll() {
        for ((_, request) in currentRequestMap) {
            cancelAndRemoveFromMap(request)
        }
    }

    fun getStatus(downloadId: Int): Status {
        val request = currentRequestMap[downloadId]
        return if (request != null) {
            request.status
        } else Status.UNKNOWN
    }

    fun addRequest(request: DownloadRequest) {
        currentRequestMap[request.downloadId] = request
        request.status = Status.QUEUED
        request.sequenceNumber = sequenceNumber
        request.future = getInstance()
            ?.executorSupplier
            ?.forDownloadTasks()
            ?.submit(DownloadRunnable(request))
    }

    fun finish(request: DownloadRequest) {
        currentRequestMap.remove(request.downloadId)
    }

    companion object {
        @JvmStatic
        var instance: DownloadRequestQueue? = null
            get() {
                if (field == null) {
                    synchronized(DownloadRequestQueue::class.java) {
                        if (field == null) {
                            field = DownloadRequestQueue()
                        }
                    }
                }
                return field
            }
            private set

        @JvmStatic
        fun initialize() {
            instance
        }
    }

    init {
        currentRequestMap = ConcurrentHashMap()
        sequenceGenerator = AtomicInteger()
    }
}