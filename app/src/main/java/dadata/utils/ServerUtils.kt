package dadata.utils

import android.os.Handler
import dadata.rest.DaDataRestClient.Companion.instance
import dadata.interfaces.OnSuggestionsListener
import io.realm.RealmResults
import dadata.realm.models.dadata.RealmDaDataSuggestion
import dadata.rest.DaDataBody
import retrofit.RetrofitError
import android.os.Looper
import dadata.realm.models.cache.Query
import dadata.realm.models.cache.Result
import io.realm.Realm
import io.realm.RealmList
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.Executors

object ServerUtils {
    private val executor = Executors.newSingleThreadExecutor()

    @JvmStatic
    fun query(query: String, listener: OnSuggestionsListener?) {
        val runnable = Runnable {
            val queryFromUser = query.replace("\\s+".toRegex(), " ").trim { it <= ' ' }

            if (queryFromUser.isNotEmpty()) {
                val realm = Realm.getDefaultInstance()
                val queryRealmResults = realm.where(
                    Query::class.java
                ).equalTo("query", queryFromUser).findAll()
                val suggestions: MutableList<String?> = ArrayList(10)
                var success = false
                if (queryRealmResults.size == 0) {
                    var suggestion: RealmDaDataSuggestion? = null
                    try {
                        suggestion = instance?.suggestSync(DaDataBody(queryFromUser, 10))
                        success = true
                    } catch (e: RetrofitError) {
                        e.printStackTrace()
                        dispatchError(e.message, listener)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dispatchError(e.message, listener)
                    }
                    if (success) {
                        cacheUserQueryWithServerResult(
                            queryFromUser,
                            realm,
                            suggestions,
                            suggestion
                        )
                    }
                } else {
                    fillSuggestionsFromCache(queryRealmResults, suggestions)
                }
                realm.close()
                dispatchUpdate(suggestions, listener)
            }
        }
        executor.submit(runnable)
    }

    private fun dispatchError(message: String?, listener: OnSuggestionsListener?) {
        if (listener != null) {
            val handler = Handler(Looper.getMainLooper())
            handler.post { listener.onError(message) }
        }
    }

    private fun fillSuggestionsFromCache(
        queryRealmResults: RealmResults<Query>,
        suggestions: MutableList<String?>
    ) {
        for (i in queryRealmResults.indices) {
            val result = queryRealmResults[i]?.result
            for (j in result!!.indices) {
                suggestions.add(result[j]?.result)
            }
        }
    }

    private fun cacheUserQueryWithServerResult(
        queryFromUser: String,
        realm: Realm,
        suggestions: MutableList<String?>,
        suggestion: RealmDaDataSuggestion?
    ) {
        val resultsRealm = RealmList<Result>()
        if (suggestion != null) {
            for (i in suggestion.suggestions!!.indices) {
                val suggestionResult = suggestion.suggestions!![i]?.value
                suggestions.add(suggestionResult)
                realm.beginTransaction()
                val result = realm.createObject(
                    Result::class.java
                )
                result.result = suggestionResult
                resultsRealm.add(result)
                realm.commitTransaction()
            }
        }
    }

    private fun dispatchUpdate(suggestions: List<String?>, listener: OnSuggestionsListener?) {
        if (listener != null && suggestions.isNotEmpty()) {
            val handler = Handler(Looper.getMainLooper())
            handler.post { listener.onSuggestionsReady(suggestions) }
        }
    }
}