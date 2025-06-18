package dadata.rest

import dadata.realm.models.dadata.RealmDaDataSuggestion
import kotlin.jvm.Volatile
import com.google.gson.GsonBuilder
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import io.realm.RealmObject
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RequestInterceptor.RequestFacade
import retrofit.converter.GsonConverter
import ru.crmkurgan.main.BuildConfig

class DaDataRestClient private constructor() {
    private val apiService: DaDataService
    fun suggestAsync(body: DaDataBody?, callback: Callback<RealmDaDataSuggestion?>?) {
        apiService.getSuggestionAsync(body, callback)
    }

    fun suggestSync(body: DaDataBody?): RealmDaDataSuggestion {
        return apiService.getSuggestionSync(body)
    }

    companion object {
        private const val BASE_URL = "https://dadata.ru"

        @JvmStatic
        @Volatile
        var instance: DaDataRestClient? = null
            get() {
                var localInstance = field
                if (localInstance == null) {
                    synchronized(DaDataRestClient::class.java) {
                        localInstance = field
                        if (localInstance == null) {
                            localInstance = DaDataRestClient()
                            field = localInstance
                        }
                    }
                }
                return localInstance
            }
            private set
    }

    init {
        val gson = GsonBuilder()
            .setExclusionStrategies(object : ExclusionStrategy {
                override fun shouldSkipField(f: FieldAttributes): Boolean {
                    return f.declaringClass == RealmObject::class.java
                }

                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                    return false
                }
            }).create()
        val restAdapter = RestAdapter.Builder()
            .setLogLevel(if (BuildConfig.DEBUG) RestAdapter.LogLevel.FULL else RestAdapter.LogLevel.NONE)
            .setEndpoint(BASE_URL)
            .setRequestInterceptor { request: RequestFacade ->
                request.addHeader("Content-Type", "application/json")
                request.addHeader("Accept", "application/json")
                request.addHeader("Authorization", "Token " + BuildConfig.DADATA_API_KEY)
            }
            .setConverter(GsonConverter(gson))
            .build()
        apiService = restAdapter.create(DaDataService::class.java)
    }
}