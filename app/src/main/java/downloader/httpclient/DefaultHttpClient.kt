package downloader.httpclient

import downloader.Constants
import downloader.request.DownloadRequest
import java.io.IOException
import java.io.InputStream
import java.lang.NumberFormatException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.util.*
import kotlin.Throws

class DefaultHttpClient : HttpClient {
    private var connection: URLConnection? = null
    override fun clone(): HttpClient {
        return DefaultHttpClient()
    }

    @Throws(IOException::class)
    override fun connect(request: DownloadRequest) {
        val connection1 = URL(request.url).openConnection()
        connection1.let {
            it?.readTimeout = request.readTimeout
            it?.connectTimeout = request.connectTimeout
            val range = String.format(
                Locale.ENGLISH,
                "bytes=%d-", request.downloadedBytes
            )
            it?.addRequestProperty(Constants.RANGE, range)
            it?.addRequestProperty(Constants.USER_AGENT, request.userAgent)
            addHeaders(request)
            it?.connect()
        }
        connection = connection1
    }

    @Throws(IOException::class)
    override fun getResponseCode(): Int {
        var responseCode = 0
        val connection1 = connection
        if (connection1 is HttpURLConnection) {
            responseCode = connection1.responseCode
        }
        return responseCode
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        return connection!!.getInputStream()
    }

    override fun getContentLength(): Long {
        val length = connection?.getHeaderField("Content-Length")
        return try {
            length!!.toLong()
        } catch (e: NumberFormatException) {
            -1
        }
    }

    override fun getResponseHeader(name: String): String {
        return connection!!.getHeaderField(name)
    }

    override fun close() {
    }

    override fun getHeaderFields(): Map<String, List<String>> {
        return connection!!.headerFields
    }

    override fun getErrorStream(): InputStream? {
        val connection1 = connection
        return if (connection1 is HttpURLConnection) {
            connection1.errorStream
        } else null
    }

    private fun addHeaders(request: DownloadRequest) {
        val headers = request.headers
        if (headers != null) {
            val entries: Set<Map.Entry<String, List<String>>> = headers.entries
            for ((name, list) in entries) {
                for (value in list) {
                    connection?.addRequestProperty(name, value)
                }
            }
        }
    }
}