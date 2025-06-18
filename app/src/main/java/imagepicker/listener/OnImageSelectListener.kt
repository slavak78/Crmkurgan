package imagepicker.listener

import imagepicker.model.Image

interface OnImageSelectListener {
    fun onSelectedImagesChanged(selectedImages: ArrayList<Image>)
    fun onSingleModeImageSelected(image: Image)
}