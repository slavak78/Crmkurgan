package ru.crmkurgan.main.Item;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import ratingview.RatingView;
import ru.crmkurgan.main.Activity.PropertyDetailActivity;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderItem extends PagerAdapter {

    private final Activity context;
    private final ArrayList<PropertyModels> mList;

    public SliderItem(Activity context, ArrayList<PropertyModels> propertyModels) {
        this.context = context;
        this.mList = propertyModels;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
        assert imageLayout != null;
        RelativeLayout rootlayout = imageLayout.findViewById(R.id.rootLayout);
        ImageView imageView = imageLayout.findViewById(R.id.image);
        TextView name = imageLayout.findViewById(R.id.text);
        TextView address = imageLayout.findViewById(R.id.address);
        Button price = imageLayout.findViewById(R.id.price);
        RatingView ratingView = imageLayout.findViewById(R.id.ratingView);

        final PropertyModels propertyModels = mList.get(position);
        Picasso.get()
                .load(propertyModels.getImage()).fit().centerCrop()
                .placeholder(R.drawable.image_placeholder).into(imageView);
        Spanned html = Html.fromHtml(propertyModels.getName());
        name.setText(html);
        price.setText(propertyModels.getPrice());
        address.setText(propertyModels.getAddress());
        ratingView.setRating(Float.parseFloat(propertyModels.getRateAvg()));

        rootlayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, PropertyDetailActivity.class);
            intent.putExtra("Id", propertyModels.getPropid());
            intent.putExtra("Tabl", propertyModels.getTabl());
            context.startActivity(intent);

        });
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}
