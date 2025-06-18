package dadata.ui

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class DaDataArrayAdapter<String>(
    context: Context?,
    resource: Int,
    private val items: List<String>
) : ArrayAdapter<String>(
    context!!, resource, items
) {
    private val filter: Filter = KNoFilter()
    override fun getFilter(): Filter {
        return filter
    }

    private inner class KNoFilter : Filter() {
        override fun performFiltering(arg0: CharSequence): FilterResults {
            val result = FilterResults()
            val items1=items
            result.values = items1
            result.count = items1.size
            return result
        }

        override fun publishResults(arg0: CharSequence, arg1: FilterResults) {
            notifyDataSetChanged()
        }
    }
}