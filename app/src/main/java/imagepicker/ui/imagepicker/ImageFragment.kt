package imagepicker.ui.imagepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ru.crmkurgan.main.R
import imagepicker.helper.ImageHelper
import imagepicker.helper.LayoutManagerHelper
import imagepicker.listener.OnImageSelectListener
import imagepicker.model.CallbackStatus
import imagepicker.model.Image
import imagepicker.model.Result
import imagepicker.ui.adapter.ImagePickerAdapter
import imagepicker.widget.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.imagepicker_fragment.*
import kotlinx.android.synthetic.main.imagepicker_fragment.view.*

class ImageFragment : BaseFragment() {

    private var bucketId: Long? = null
    private lateinit var viewModel: ImagePickerViewModel
    private lateinit var imageAdapter: ImagePickerAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var itemDecoration: GridSpacingItemDecoration

    companion object {

        const val BUCKET_ID = "BucketId"

        fun newInstance(bucketId: Long): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putLong(BUCKET_ID, bucketId)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(): ImageFragment {
            return ImageFragment()
        }
    }

    private val selectedImageObserver = object : Observer<ArrayList<Image>> {
        override fun onChanged(it: ArrayList<Image>) {
            imageAdapter.setSelectedImages(it)
            viewModel.selectedImages.removeObserver(this)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bucketId = arguments?.getLong(BUCKET_ID)
        viewModel = activity!!.run {
            ViewModelProvider(this, ImagePickerViewModelFactory(activity!!.application)).get(ImagePickerViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.imagepicker_fragment, container, false)
        root.setBackgroundColor(viewModel.getConfig()
            .getBackgroundColor())

        imageAdapter = ImagePickerAdapter(activity!!, viewModel.getConfig(), activity as OnImageSelectListener)
        gridLayoutManager = LayoutManagerHelper.newInstance(context!!)
        itemDecoration = GridSpacingItemDecoration(gridLayoutManager.spanCount, gridLayoutManager.spanCount, false)
        with(root.recyclerView) {
            this.layoutManager = gridLayoutManager
            setHasFixedSize(true)
            addItemDecoration(itemDecoration)
            this.adapter = imageAdapter
        }

        viewModel.result.observe(viewLifecycleOwner, Observer {
            handleResult(it)
        })

        viewModel.selectedImages.observe(viewLifecycleOwner, selectedImageObserver)

        return root
    }


    private fun handleResult(result: Result) {
        if (result.status is CallbackStatus.SUCCESS) {
            val images = ImageHelper.filterImages(result.images, bucketId)
            if (images.isNotEmpty()) {
                imageAdapter.setData(images)
                recyclerView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.GONE
            }
        } else {
            recyclerView.visibility = View.GONE
        }
        emptyText.visibility = if (result.status is CallbackStatus.SUCCESS && result.images.isEmpty()) View.VISIBLE else View.GONE
        progressWheel.visibility = if (result.status is CallbackStatus.FETCHING) View.VISIBLE else View.GONE
    }

    override fun handleOnConfigurationChanged() {
        val newSpanCount = LayoutManagerHelper.getSpanCountForCurrentConfiguration(context!!, false)
        recyclerView.removeItemDecoration(itemDecoration)
        itemDecoration = GridSpacingItemDecoration(newSpanCount, newSpanCount, false)
        gridLayoutManager.spanCount = newSpanCount
        recyclerView.addItemDecoration(itemDecoration)
    }

}