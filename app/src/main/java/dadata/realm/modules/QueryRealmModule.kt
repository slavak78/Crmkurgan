package dadata.realm.modules

import dadata.realm.models.cache.Query
import dadata.realm.models.cache.Result
import io.realm.annotations.RealmModule

@RealmModule(classes = [Query::class, Result::class])
class QueryRealmModule 