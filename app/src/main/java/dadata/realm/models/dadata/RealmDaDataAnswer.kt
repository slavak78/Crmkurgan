package dadata.realm.models.dadata

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import com.google.gson.annotations.SerializedName

open class RealmDaDataAnswer : RealmObject() {
    @PrimaryKey
    var uuid: String? = null
    var value: String? = null
    var unrestrictedValue: String? = null

    @SerializedName("data")
    var realmData: RealmData? = null
}