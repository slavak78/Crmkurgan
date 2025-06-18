package ru.crmkurgan.main.Item;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.TouchImageView;

import java.util.List;

public class FullImageItem extends PagerAdapter {


    private final Activity act;
    private final List<String> imagePaths;
    private final ImageLoader imgloader = ImageLoader.getInstance();

    public FullImageItem (Activity activity, List<String> imagePaths) {
        this.act = activity;
        this.imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this.imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TouchImageView imgDisplay;
        LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.item_full_image, container, false);

        imgDisplay = viewLayout.findViewById(R.id.imgDisplay);

        imgloader.displayImage(imagePaths.get(position), imgDisplay);
        container.addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);

    }

}
