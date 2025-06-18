package downloader.internal

import android.content.Context
import downloader.Constants
import downloader.database.DbHelper
import downloader.PRDownloaderConfig
import downloader.database.AppDbHelper
import downloader.database.NoOpsDbHelper
import downloader.PRDownloader
import downloader.httpclient.DefaultHttpClient
import downloader.httpclient.HttpClient

class ComponentHolder {
    private var readTimeout = 0
    private var connectTimeout = 0
    private var userAgent: String? = null
    private var httpClient: HttpClient? = null
    private var dbHelper: DbHelper? = null
    fun init(context: Context?, config: PRDownloaderConfig) {
        readTimeout = config.readTimeout
        connectTimeout = config.connectTimeout
        userAgent = config.userAgent
        httpClient = config.httpClient
        dbHelper = if (config.isDatabaseEnabled) AppDbHelper(context) else NoOpsDbHelper()
        if (config.isDatabaseEnabled) {
            PRDownloader.cleanUp(30)
        }
    }

    fun getReadTimeout(): Int {
        var readTimeout1=readTimeout
        if (readTimeout1 == 0) {
            synchronized(ComponentHolder::class.java) {
                if (readTimeout1 == 0) {
                    readTimeout1 = Constants.DEFAULT_READ_TIMEOUT_IN_MILLS
                }
            }
        }
        return readTimeout1
    }

    fun getConnectTimeout(): Int {
        var connectTimeout1=connectTimeout
        if (connectTimeout1 == 0) {
            synchronized(ComponentHolder::class.java) {
                if (connectTimeout1 == 0) {
                    connectTimeout1 = Constants.DEFAULT_CONNECT_TIMEOUT_IN_MILLS
                }
            }
        }
        return connectTimeout1
    }

    fun getUserAgent(): String? {
        if (userAgent == null) {
            synchronized(ComponentHolder::class.java) {
                if (userAgent == null) {
                    userAgent = Constants.DEFAULT_USER_AGENT
                }
            }
        }
        return userAgent
    }

    fun getDbHelper(): DbHelper? {
        var dbHelper1=dbHelper
        if (dbHelper1 == null) {
            synchronized(ComponentHolder::class.java) {
                if (dbHelper1 == null) {
                    dbHelper1 = NoOpsDbHelper()
                }
            }
        }
        return dbHelper1
    }

    fun getHttpClient(): HttpClient {
        var httpClient1=httpClient
        if (httpClient1 == null) {
            synchronized(ComponentHolder::class.java) {
                if (httpClient1 == null) {
                    httpClient1 = DefaultHttpClient()
                }
            }
        }
        return httpClient1!!.clone()
    }

    companion object {
        val instance = ComponentHolder()
    }
}