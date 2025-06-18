package dadata.interfaces

interface OnSuggestionsListener {
    fun onSuggestionsReady(suggestions: List<String?>?)
    fun onError(message: String?)
}