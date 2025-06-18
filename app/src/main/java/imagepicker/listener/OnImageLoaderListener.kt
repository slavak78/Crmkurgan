package imagepicker.listener

import imagepicker.model.Image

interface OnImageLoaderListener {
    fun onImageLoaded(images: ArrayList<Image>)
    fun onFailed(throwable: Throwable)
}