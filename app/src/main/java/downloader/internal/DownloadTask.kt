package downloader.internal

import downloader.Constants
import downloader.Error
import downloader.internal.stream.FileDownloadRandomAccessFile.Companion.create
import downloader.handler.ProgressHandler
import downloader.internal.stream.FileDownloadOutputStream
import downloader.database.DownloadModel
import kotlin.Throws
import downloader.Progress
import downloader.Response
import downloader.Status
import downloader.httpclient.HttpClient
import downloader.request.DownloadRequest
import downloader.utils.Utils
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection

class DownloadTask private constructor(private val request: DownloadRequest) {
    private var progressHandler: ProgressHandler? = null
    private var lastSyncTime: Long = 0
    private var lastSyncBytes: Long = 0
    private var inputStream: InputStream? = null
    private var outputStream: FileDownloadOutputStream? = null
    private var httpClient: HttpClient? = null
    private var totalBytes: Long = 0
    private var responseCode = 0
    private var eTag: String? = null
    private var isResumeSupported = false
    private var tempPath: String? = null
    fun run(): Response {
        val response = Response()
        val request1 = request
        val status = request1.status
        if (status == Status.CANCELLED) {
            response.isCancelled = true
            return response
        } else if (status == Status.PAUSED) {
            response.isPaused = true
            return response
        }
        try {
            if (request1.onProgressListener != null) {
                progressHandler = ProgressHandler(request1.onProgressListener)
            }
            tempPath = Utils.getTempPath(request1.dirPath, request1.fileName)
            val tempPath1=tempPath
            val file = tempPath1?.let { File(it) }
            var model: DownloadModel? = downloadModelIfAlreadyPresentInDatabase
            model?.let { mod ->
                file?.let {
                    if (it.exists()) {
                        request1.totalBytes = mod.totalBytes
                        request1.downloadedBytes = mod.downloadedBytes
                    } else {
                        removeNoMoreNeededModelFromDatabase()
                        request1.downloadedBytes = 0
                        request1.totalBytes = 0
                        model = null
                    }
                }
            }
            httpClient = ComponentHolder.instance.getHttpClient()
            var httpClient1 = httpClient
            httpClient1?.let {
                it.connect(request1)
                if (status == Status.CANCELLED) {
                    response.isCancelled = true
                    return response
                } else if (status == Status.PAUSED) {
                    response.isPaused = true
                    return response
                }
                httpClient1 = Utils.getRedirectedConnectionIfAny(httpClient1, request1)
                responseCode = it.responseCode
                eTag = it.getResponseHeader(Constants.ETAG)
                if (checkIfFreshStartRequiredAndStart(model)) {
                    model = null
                }
                if (!isSuccessful) {
                    val error = Error()
                    error.isServerError = true
                    error.serverErrorMessage = convertStreamToString(it.errorStream)
                    error.headerFields = it.headerFields
                    error.responseCode = responseCode
                    response.error = error
                    return response
                }
                setResumeSupportedOrNot()
                totalBytes = request1.totalBytes
                if (!isResumeSupported) {
                    deleteTempFile()
                }
                if (totalBytes == 0L) {
                    totalBytes = it.contentLength
                    request1.totalBytes = totalBytes
                }
                if (isResumeSupported && model == null) {
                    createAndInsertNewModel()
                }
                if (status == Status.CANCELLED) {
                    response.isCancelled = true
                    return response
                } else if (status == Status.PAUSED) {
                    response.isPaused = true
                    return response
                }
                request.deliverStartEvent()
                inputStream = it.inputStream
                val buff = ByteArray(BUFFER_SIZE)
                file?.let { fil ->
                    if (!fil.exists()) {
                        val parentFile = fil.parentFile
                        if (parentFile != null && !parentFile.exists()) {
                            if (parentFile.mkdirs()) {
                                fil.createNewFile()
                            }
                        } else {
                            fil.createNewFile()
                        }
                    }
                }
                outputStream = file?.let { it1 -> create(it1) }
                if (isResumeSupported && request.downloadedBytes != 0L) {
                    outputStream?.seek(request.downloadedBytes)
                }
                if (status == Status.CANCELLED) {
                    response.isCancelled = true
                    return response
                } else if (status == Status.PAUSED) {
                    response.isPaused = true
                    return response
                }
                do {
                    val byteCount = inputStream?.read(buff, 0, BUFFER_SIZE)
                    if (byteCount == -1) {
                        break
                    }
                    byteCount?.let { count ->
                        outputStream?.write(buff, 0, count)
                        request1.downloadedBytes = request1.downloadedBytes + count
                    }
                    sendProgress()
                    outputStream?.let { it1 -> syncIfRequired(it1) }
                    if (status == Status.CANCELLED) {
                        response.isCancelled = true
                        return response
                    } else if (status == Status.PAUSED) {
                        outputStream?.let { it1 -> sync(it1) }
                        response.isPaused = true
                        return response
                    }
                } while (true)
                val path = Utils.getPath(request1.dirPath, request1.fileName)
                Utils.renameFileName(tempPath, path)
                response.isSuccessful = true
                if (isResumeSupported) {
                    removeNoMoreNeededModelFromDatabase()
                }
            }
        } catch (e: IOException) {
            if (!isResumeSupported) {
                deleteTempFile()
            }
            val error = Error()
            error.isConnectionError = true
            error.connectionException = e
            response.error = error
        } catch (e: IllegalAccessException) {
            if (!isResumeSupported) {
                deleteTempFile()
            }
            val error = Error()
            error.isConnectionError = true
            error.connectionException = e
            response.error = error
        } finally {
            closeAllSafely(outputStream)
        }
        return response
    }

    private fun deleteTempFile() {
        val file = File(tempPath)
        if (file.exists()) {
            file.delete()
        }
    }

    private val isSuccessful: Boolean
        private get() = (responseCode >= HttpURLConnection.HTTP_OK
                && responseCode < HttpURLConnection.HTTP_MULT_CHOICE)

    private fun setResumeSupportedOrNot() {
        isResumeSupported = responseCode == HttpURLConnection.HTTP_PARTIAL
    }

    @Throws(IOException::class, IllegalAccessException::class)
    private fun checkIfFreshStartRequiredAndStart(model: DownloadModel?): Boolean {
        if (responseCode == Constants.HTTP_RANGE_NOT_SATISFIABLE || isETagChanged(model)) {
            if (model != null) {
                removeNoMoreNeededModelFromDatabase()
            }
            deleteTempFile()
            request.downloadedBytes = 0
            request.totalBytes = 0
            httpClient = ComponentHolder.getInstance().getHttpClient()
            httpClient!!.connect(request)
            httpClient = Utils.getRedirectedConnectionIfAny(httpClient, request)
            responseCode = httpClient.responseCode
            return true
        }
        return false
    }

    private fun isETagChanged(model: DownloadModel?): Boolean {
        return (!(eTag == null || model == null || model.eTag == null)
                && model.eTag != eTag)
    }

    private val downloadModelIfAlreadyPresentInDatabase: DownloadModel
        private get() = ComponentHolder.getInstance().getDbHelper().find(request.downloadId)

    private fun createAndInsertNewModel() {
        val model = DownloadModel()
        model.id = request.downloadId
        model.url = request.url
        model.eTag = eTag
        model.dirPath = request.dirPath
        model.fileName = request.fileName
        model.downloadedBytes = request.downloadedBytes
        model.totalBytes = totalBytes
        model.lastModifiedAt = System.currentTimeMillis()
        ComponentHolder.getInstance().getDbHelper().insert(model)
    }

    private fun removeNoMoreNeededModelFromDatabase() {
        ComponentHolder.getInstance().getDbHelper().remove(request.downloadId)
    }

    private fun sendProgress() {
        if (request.status != Status.CANCELLED) {
            if (progressHandler != null) {
                progressHandler!!
                    .obtainMessage(
                        Constants.UPDATE,
                        Progress(
                            request.downloadedBytes,
                            totalBytes
                        )
                    ).sendToTarget()
            }
        }
    }

    private fun syncIfRequired(outputStream: FileDownloadOutputStream) {
        val currentBytes = request.downloadedBytes
        val currentTime = System.currentTimeMillis()
        val bytesDelta = currentBytes - lastSyncBytes
        val timeDelta = currentTime - lastSyncTime
        if (bytesDelta > MIN_BYTES_FOR_SYNC && timeDelta > TIME_GAP_FOR_SYNC) {
            sync(outputStream)
            lastSyncBytes = currentBytes
            lastSyncTime = currentTime
        }
    }

    private fun sync(outputStream: FileDownloadOutputStream) {
        var success: Boolean
        try {
            outputStream.flushAndSync()
            success = true
        } catch (e: IOException) {
            success = false
            e.printStackTrace()
        }
        if (success && isResumeSupported) {
            ComponentHolder.getInstance().getDbHelper()
                .updateProgress(
                    request.downloadId,
                    request.downloadedBytes,
                    System.currentTimeMillis()
                )
        }
    }

    private fun closeAllSafely(outputStream: FileDownloadOutputStream?) {
        if (httpClient != null) {
            try {
                httpClient!!.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (inputStream != null) {
            try {
                inputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            if (outputStream != null) {
                try {
                    sync(outputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } finally {
            if (outputStream != null) try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun convertStreamToString(stream: InputStream?): String {
        val stringBuilder = StringBuilder()
        if (stream != null) {
            var line: String?
            try {
                BufferedReader(InputStreamReader(stream)).use { bufferedReader ->
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                }
            } catch (ignored: IOException) {
            }
        }
        return stringBuilder.toString()
    }

    companion object {
        private const val BUFFER_SIZE = 1024 * 4
        private const val TIME_GAP_FOR_SYNC: Long = 2000
        private const val MIN_BYTES_FOR_SYNC: Long = 65536

        @JvmStatic
        fun create(request: DownloadRequest): DownloadTask {
            return DownloadTask(request)
        }
    }
}