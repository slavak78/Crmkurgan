package imagepicker.model

sealed class CallbackStatus {
    object IDLE : CallbackStatus()
    object FETCHING : CallbackStatus()
    object SUCCESS : CallbackStatus()
}