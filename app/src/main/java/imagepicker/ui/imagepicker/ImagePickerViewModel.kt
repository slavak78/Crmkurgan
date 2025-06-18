package imagepicker.ui.imagepicker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import imagepicker.listener.OnImageLoaderListener
import imagepicker.model.CallbackStatus
import imagepicker.model.Config
import imagepicker.model.Image
import imagepicker.model.Result

class ImagePickerViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val imageFileLoader: ImageFileLoader = ImageFileLoader(context)
    private lateinit var config: Config

    lateinit var selectedImages: MutableLiveData<ArrayList<Image>>
    val result = MutableLiveData(Result(CallbackStatus.IDLE, arrayListOf()))

    fun setConfig(config: Config) {
        this.config = config
        selectedImages = MutableLiveData(config.selectedImages)
    }

    fun getConfig() = config

    fun fetchImages() {
        result.postValue(Result(CallbackStatus.FETCHING, arrayListOf()))
        imageFileLoader.abortLoadImages()
        imageFileLoader.loadDeviceImages(object : OnImageLoaderListener {
            override fun onImageLoaded(images: ArrayList<Image>) {
                result.postValue(Result(CallbackStatus.SUCCESS, images))
            }

            override fun onFailed(throwable: Throwable) {
                result.postValue(Result(CallbackStatus.SUCCESS, arrayListOf()))
            }

        })
    }
}