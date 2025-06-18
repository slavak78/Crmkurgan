package dadata.realm.models.cache

import io.realm.RealmObject
import io.realm.annotations.Index

open class Result : RealmObject() {
    @Index
    var result: String? = null
}