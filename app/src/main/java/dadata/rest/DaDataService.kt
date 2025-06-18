package dadata.rest

import retrofit.http.POST
import retrofit.http.Body
import dadata.realm.models.dadata.RealmDaDataSuggestion
import retrofit.Callback

interface DaDataService {
    @POST("/api/v2/suggest/address")
    fun getSuggestionSync(@Body body: DaDataBody?): RealmDaDataSuggestion?

    @POST("/api/v2/suggest/address")
    fun getSuggestionAsync(@Body body: DaDataBody?, callback: Callback<RealmDaDataSuggestion?>?)
}