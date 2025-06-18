package dadata.realm.models.dadata

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmData : RealmObject() {
    @PrimaryKey
    var uuid: String? = null
    var qc_complete: String? = null
    var qc_house: String? = null
    var qc_geo: String? = null
    var postal_code: String? = null
    var postal_box: String? = null
    var country: String? = null
    var region_with_type: String? = null
    var region_type: String? = null
    var region_type_full: String? = null
    var region: String? = null
    var area_with_type: String? = null
    var area_type: String? = null
    var area_type_full: String? = null
    var area: String? = null
    var city_with_type: String? = null
    var city_type: String? = null
    var city_type_full: String? = null
    var city: String? = null
    var city_district: String? = null
    var settlement_with_type: String? = null
    var settlement_type: String? = null
    var settlement_type_full: String? = null
    var settlement: String? = null
    var street_with_type: String? = null
    var street_type: String? = null
    var street_type_full: String? = null
    var street: String? = null
    var house_type: String? = null
    var house_type_full: String? = null
    var house: String? = null
    var block_type: String? = null
    var block: String? = null
    var flat_area: String? = null
    var flat_type: String? = null
    var flat: String? = null
    var fias_id: String? = null
    var fias_level: String? = null
    var kladr_id: String? = null
    var tax_office: String? = null
    var tax_office_legal: String? = null
    var capital_marker: String? = null
    var okato: String? = null
    var oktmo: String? = null
    var geo_lat: String? = null
    var geo_lon: String? = null
    var unparsed_parts: String? = null
    var qc: String? = null
}