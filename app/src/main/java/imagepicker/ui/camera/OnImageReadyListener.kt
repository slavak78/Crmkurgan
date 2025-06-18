package imagepicker.ui.camera

import imagepicker.model.Image

interface OnImageReadyListener {
    fun onImageReady(images: ArrayList<Image>)
    fun onImageNotReady()
}