package dadata.realm.models.cache

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.RealmList
import io.realm.annotations.Index

open class Query : RealmObject() {
    @PrimaryKey
    var id: String? = null

    @Index
    var query: String? = null
    var result: RealmList<Result>? = null
}