package ru.crmkurgan.main.Activity;

import static ru.crmkurgan.main.Utils.Tools.setSystemBarColorBlack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.ImageLoader;
import ru.crmkurgan.main.Item.FullImageItem;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.Tools;

import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity {

    public static final String EXTRA_POS = "key.EXTRA_POS";
    public static final String EXTRA_IMGS = "key.EXTRA_IMGS";

    private final ImageLoader imgloader = ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        setSystemBarColorBlack(this);
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ArrayList<String> items;
        Intent i = getIntent();
        final int position = i.getIntExtra(EXTRA_POS, 0);
        items = i.getStringArrayListExtra(EXTRA_IMGS);
        FullImageItem adapter = new FullImageItem(FullImageActivity.this, items);
        final int total = adapter.getCount();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        ((ImageView) findViewById(R.id.btnClose)).setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        super.onResume();
    }

}
