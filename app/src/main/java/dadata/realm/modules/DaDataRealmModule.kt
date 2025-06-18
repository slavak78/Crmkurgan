package dadata.realm.modules

import io.realm.annotations.RealmModule
import dadata.realm.models.dadata.RealmDaDataAnswer
import dadata.realm.models.dadata.RealmData
import dadata.realm.models.dadata.RealmDaDataSuggestion

@RealmModule(classes = [RealmDaDataAnswer::class, RealmData::class, RealmDaDataSuggestion::class])
class DaDataRealmModule 