@file:Suppress("DEPRECATION")

package httpclient.request

import android.annotation.SuppressLint
import android.net.Uri
import httpclient.common.HeaderBag
import httpclient.response.ResponseListener
import httpclient.response.FileDownloadListener
import httpclient.response.FileResponseListener
import httpclient.util.ByteStream
import android.os.AsyncTask
import android.util.Log
import httpclient.common.Header
import httpclient.debug.Logger
import imagepicker.model.Image
import java.io.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*
import kotlin.Throws

class RequestHttp private constructor() {
    var url: URL? = null
        private set
    var method: String? = null
        private set
    var parameters: ParameterBag
        private set
    private var headers: HeaderBag
    private val boundary: String = java.lang.Long.toHexString(System.currentTimeMillis())
    private var responseListener: ResponseListener? = null
    private var fileUploadListener: FileUploadListener? = null
    private var fileDownloadListener: FileDownloadListener? = null
    private var requestStateListener: RequestStateListener? = null
    private var runner: ProgressTask? = null
    private var logger: Logger? = null
    private var timeout = DEFAULT_TIMEOUT * 1000
    fun setUrl(url: URL?): RequestHttp {
        this.url = url
        return this
    }

    fun setMethod(method: String): RequestHttp {
        this.method = method.uppercase(Locale.getDefault())
        return this
    }

    fun setParameters(parameters: ParameterBag): RequestHttp {
        this.parameters = parameters
        return this
    }

    fun addParameters(parameters: ParameterBag): RequestHttp {
        this.parameters.addAll(parameters)
        return this
    }

    fun addParameter(parameter: Parameter): RequestHttp {
        parameters.add(parameter)
        return this
    }

    fun addParameter(key: String?, value: String?): RequestHttp {
        parameters.add(key, value)
        return this
    }

    fun addParameter(key: String?, value: Boolean): RequestHttp {
        parameters.add(key, value)
        return this
    }

    fun addParameter(key: String?, value: Long): RequestHttp {
        parameters.add(key, value)
        return this
    }

    fun addParameter(key: String?, value: Double): RequestHttp {
        parameters.add(key, value)
        return this
    }

    fun addParameter(key: String?, value: File?): RequestHttp {
        parameters.add(key, value)
        return this
    }

    fun addParameter(key: String?, images: ArrayList<Image>): RequestHttp {
        for ((_, _, _, path) in images) {
            parameters.add(key, File(path))
        }
        return this
    }

    fun setHeaders(headers: HeaderBag): RequestHttp {
        this.headers = headers
        return this
    }

    fun addHeaders(headers: Collection<Header?>): RequestHttp {
        this.headers.addAll(headers)
        return this
    }

    fun addHeader(header: String?, value: String?): RequestHttp {
        headers.add(header, value)
        return this
    }

    fun addHeader(key: String?, value: Boolean): RequestHttp {
        headers.add(key, value)
        return this
    }

    fun addHeader(key: String?, value: Long): RequestHttp {
        headers.add(key, value)
        return this
    }

    fun addHeader(key: String?, value: Double): RequestHttp {
        headers.add(key, value)
        return this
    }

    fun setResponseListener(responseListener: ResponseListener?): RequestHttp {
        this.responseListener = responseListener
        return this
    }

    fun setFileUploadListener(fileUploadListener: FileUploadListener?): RequestHttp {
        this.fileUploadListener = fileUploadListener
        return this
    }

    fun setFileDownloadListener(fileDownloadListener: FileDownloadListener?): RequestHttp {
        this.fileDownloadListener = fileDownloadListener
        return this
    }

    fun setRequestStateListener(requestStateListener: RequestStateListener?): RequestHttp {
        this.requestStateListener = requestStateListener
        return this
    }

    fun setTimeout(timeoutInSeconds: Int): RequestHttp {
        timeout = timeoutInSeconds * 1000
        return this
    }

    fun setLogger(logger: Logger?): RequestHttp {
        this.logger = logger
        return this
    }

    @Suppress("DEPRECATION")
    fun execute() {
        runner = @SuppressLint("StaticFieldLeak")
        object : ProgressTask() {
            var fireOnFinish = true

            @Deprecated("Deprecated in Java")
            override fun doInBackground(vararg p0: Void?): Void? {
                try {
                    val method = method
                    val finalUrl = parseWithGet()
                    val finalURL = URL(finalUrl)
                    val connection = finalURL.openConnection() as HttpURLConnection
                    connection.connectTimeout = timeout
                    connection.doOutput = method != GET
                    connection.requestMethod = method
                    logging("URL: " + method + " " + url.toString(), Logger.DEBUG)
                    sendHeaders(connection)
                    printParams()
                    if (method != GET) {
                        val outputStream = connection.outputStream
                        sendParams(outputStream)
                    }
                    val responseCode = connection.responseCode
                    val data: ByteArray = if (responseCode in 200..399) {
                        if (responseListener is FileResponseListener) {
                            downloadFile(connection)
                            return null
                        } else {
                            ByteStream.toByteArray(connection.inputStream)
                        }
                    } else {
                        val `is` = connection.errorStream
                        if (`is` != null) {
                            ByteStream.toByteArray(connection.errorStream)
                        } else {
                            logging(
                                "No response. Invalid HTTP CODE Response? $responseCode",
                                Logger.ERROR
                            )
                            "{\"error\":\"No response\"}".toByteArray()
                        }
                    }
                    val response = String(data)
                    logging("Response: $response", Logger.DEBUG)
                    publishProgress(responseCode, response)
                    connection.disconnect()
                } catch (e: IOException) {
                    logging("Error trying to perform request", Logger.ERROR, e)
                    fireOnFinish = false
                    requestStateListener?.onConnectionError(e)
                }
                return null
            }

            @Deprecated("Deprecated in Java")
            override fun onPreExecute() {
                super.onPreExecute()
                requestStateListener?.onStart()
            }

            @Deprecated("Deprecated in Java")
            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                requestStateListener?.let {
                    if (fireOnFinish) {
                        it.onFinish()
                    }
                }
            }
        }
        (runner as ProgressTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    private fun parseWithGet(): String {
        var method = method
        if (method == null) {
            method = GET
        }
        if (!method.equals(GET, ignoreCase = true)) {
            return url.toString()
        }
        val uri = Uri.parse(url.toString())
        val uriBuilder = uri.buildUpon()
        val parameters = parameters
        for (p in parameters) {
            if (p.isFile) {
                continue
            }
            uriBuilder.appendQueryParameter(p.nameParam, p.valueAsString)
        }
        return uriBuilder.build().toString()
    }

    private fun printParams() {
        logging("---- PARAMETERS ----", Logger.DEBUG)
        for (p in parameters) {
            if (p.isFile && method.equals(GET, ignoreCase = true)) {
                logging(p.nameParam + " = IGNORED FILE[" + p.file.absolutePath + "]", Logger.DEBUG)
                continue
            } else if (p.isFile) {
                logging(p.nameParam + " = FILE[" + p.file.absolutePath + "]", Logger.DEBUG)
                continue
            }
            logging(p.nameParam + " = " + String(p.paramValue), Logger.DEBUG)
        }
    }

    @Throws(IOException::class)
    @Suppress("DEPRECATION")
    private fun downloadFile(connection: HttpURLConnection) {
        val fileLength = connection.contentLength
        var bufferSize = fileLength / 100
        if (bufferSize <= 0) {
            bufferSize = 1
        }
        val respListener = responseListener
        val fileDownListener = fileDownloadListener
        val runner1 = runner
        val fileListener = respListener as FileResponseListener?
        val downloadFile = fileListener?.file
        val output: OutputStream = FileOutputStream(downloadFile)
        val input = connection.inputStream
        val fileData = ByteArray(bufferSize)
        var total: Long = 0
        var count: Int
        fileDownListener?.onDownloadStart()
        val baos = ByteArrayOutputStream()
        while (input.read(fileData).also { count = it } != -1) {
            if (runner1?.isCancelled == true) {
                input.close()
                output.close()
                fileDownListener?.onDownloadCancel()
                fileListener?.onCancel()
                return
            }
            total += count.toLong()
            baos.write(fileData, 0, count)
            fileDownListener?.onDownloadingFile(downloadFile, fileLength.toLong(), total)
        }
        val data = baos.toByteArray()
        output.write(data)
        output.flush()
        output.close()
        if (runner1?.isCancelled == false) {
            fileDownListener?.onDownloadFinish()
            respListener?.onResponse(200, String(data))
        }
    }

    @Suppress("DEPRECATION")
    fun cancel() {
        runner?.cancel(true)
    }

    @Throws(IOException::class)
    @Suppress("DEPRECATION")
    private fun sendParams(outputStream: OutputStream) {
        val param = parameters
        if (!param.isEmpty()) {
            val writer = DataOutputStream(outputStream)
            for (p in param) {
                writer.writeBytes("--$boundary$CRLF")
                writer.writeBytes(p.contentType + CRLF)
                if (p.isFile) {
                    writer.writeBytes(
                        "Content-Disposition: form-data; name=\""
                                + p.nameParam
                                + "\"; filename=\""
                                + p.file.name
                                + "\""
                                + CRLF
                    )
                    writer.writeBytes(CRLF)
                    writer.flush()
                    val data = p.paramValue
                    val fileUpListener = fileUploadListener
                    fileUpListener?.onUploadStart()
                    val bais = ByteArrayInputStream(data)
                    var bufferSize = data.size / 100
                    if (bufferSize <= 0) {
                        bufferSize = 1
                    }
                    val buff = ByteArray(bufferSize)
                    var lenght: Int
                    var progress: Long = 0
                    val file = p.file
                    while (bais.read(buff).also { lenght = it } != -1) {
                        val runner1 = runner
                        if (runner1?.isCancelled == true) {
                            bais.close()
                            outputStream.close()
                            fileUpListener?.onUploadCancel()
                            return
                        }
                        outputStream.write(buff, 0, lenght)
                        progress += lenght.toLong()
                        runner1?.publishProgress2(data.size.toLong(), progress, file)
                    }
                    outputStream.flush()
                    fileUpListener?.onUploadFinish(file)
                } else {
                    if (method != GET) {
                        writer.writeBytes(
                            "Content-Disposition: form-data; name=\""
                                    + p.nameParam
                                    + "\"" + CRLF
                        )
                        writer.writeBytes(p.contentType + CRLF)
                        writer.writeBytes(CRLF + p.valueAsString)
                    }
                }
                writer.writeBytes(CRLF)
            }
            writer.writeBytes("--$boundary--$CRLF")
        }
    }

    private fun sendHeaders(connection: URLConnection) {
        logging("---- HEADERS ----", Logger.DEBUG)
        for (h in headers) {
            val key = h.key
            if (method.equals("GET", ignoreCase = true) && key.equals(
                    "content-type",
                    ignoreCase = true
                )
            ) {
                continue
            }
            val value = h.value
            logging("$key = $value", Logger.DEBUG)
            connection.addRequestProperty(key, value)
        }
    }

    private fun logging(message: String, level: Int, e: Exception? = null) {
        val logg = logger
        when (level) {
            Logger.INFO -> logg?.i(TAG, message, e)
            Logger.DEBUG -> logg?.d(TAG, message, e)
            else -> logg?.e(TAG, message, e)
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private abstract inner class ProgressTask : AsyncTask<Void?, Any?, Void?>() {
        fun publishProgress2(vararg values: Any) {
            val length = values[0].toString().toLong()
            val progress = values[1].toString().toLong()
            val f = values[2] as File
            fileUploadListener?.let {
                Log.e(TAG, "uploading $progress / $length")
                it.onUploadingFile(f, length, progress)
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onProgressUpdate(vararg values: Any?) {
            super.onProgressUpdate(*values)
            responseListener?.let {
                val responseCode = values[0].toString().toInt()
                val response = values[1].toString()
                it.onResponse(responseCode, response)
            }
        }
    }

    companion object {
        private const val TAG = "RequestHttp"
        private const val CRLF = "\r\n"
        const val GET = "GET"
        const val POST = "POST"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
        const val OPTIONS = "OPTIONS"
        const val HEAD = "HEAD"
        const val TRACE = "TRACE"
        const val DEFAULT_TIMEOUT = 60

        @JvmStatic
        fun create(url: String?): RequestHttp {
            return try {
                val r = RequestHttp()
                r.url = URL(url)
                r
            } catch (e: MalformedURLException) {
                throw IllegalArgumentException(e)
            }
        }
    }

    init {
        parameters = ParameterBag()
        headers = HeaderBag()
        headers.add("Content-Type", "multipart/form-data; boundary=$boundary")
    }
}