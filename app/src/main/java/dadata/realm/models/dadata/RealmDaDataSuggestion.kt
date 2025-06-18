package dadata.realm.models.dadata

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.RealmList

open class RealmDaDataSuggestion : RealmObject() {
    @PrimaryKey
    var uuid: String? = null
    var suggestions: RealmList<RealmDaDataAnswer>? = null
}