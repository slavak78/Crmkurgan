package iosdialog.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import ru.crmkurgan.main.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DialogUtils {

    private static List<Drawable> petals;
    private static final int PETALS_COUNT = 12;

    private static final int DEFAULT_DURATION = 60;

    @ColorInt
    public static int getColor(Context context, @ColorRes int colorId) {
        return ContextCompat.getColor(context, colorId);
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int attr) {
        return resolveColor(context, attr, 0);
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int attr, int fallback) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attr});
        try {
            return a.getColor(0, fallback);
        } finally {
            a.recycle();
        }
    }

    public static AnimationDrawable createAnimation(Context context) {
        return (AnimationDrawable) ContextCompat.getDrawable(context, R.drawable.spinner);
    }

    public static AnimationDrawable createAnimation(Context context, @ColorInt int color) {
        return createAnimation(context, color, DEFAULT_DURATION);
    }

    public static AnimationDrawable createAnimation(Context context, @ColorInt int color, int duration) {
        return createAnimation(context, color, duration, true);

    }

    public static AnimationDrawable createAnimation(Context context, @ColorInt int color, int duration, boolean clockwise) {
        if (petals == null) {
            petals = new ArrayList<>(PETALS_COUNT);
            Drawable dr0 = ContextCompat.getDrawable(context, R.drawable.spinner_0);
            Drawable dr1 = ContextCompat.getDrawable(context, R.drawable.spinner_1);
            Drawable dr2 = ContextCompat.getDrawable(context, R.drawable.spinner_2);
            Drawable dr3 = ContextCompat.getDrawable(context, R.drawable.spinner_3);
            Drawable dr4 = ContextCompat.getDrawable(context, R.drawable.spinner_4);
            Drawable dr5 = ContextCompat.getDrawable(context, R.drawable.spinner_5);
            Drawable dr6 = ContextCompat.getDrawable(context, R.drawable.spinner_6);
            Drawable dr7 = ContextCompat.getDrawable(context, R.drawable.spinner_7);
            Drawable dr8 = ContextCompat.getDrawable(context, R.drawable.spinner_8);
            Drawable dr9 = ContextCompat.getDrawable(context, R.drawable.spinner_9);
            Drawable dr10 = ContextCompat.getDrawable(context, R.drawable.spinner_10);
            Drawable dr11 = ContextCompat.getDrawable(context, R.drawable.spinner_11);
            Collections.addAll(petals
                    , dr0
                    , dr1
                    , dr2
                    , dr3
                    , dr4
                    , dr5
                    , dr6
                    , dr7
                    , dr8
                    , dr9
                    , dr10
                    , dr11
            );
        }
        AnimationDrawable animation = new AnimationDrawable();
        List<Drawable> drawables = new ArrayList<>(PETALS_COUNT);
        for (Drawable drawable : petals) {
            Drawable drwNewCopy = drawable.getConstantState().newDrawable().mutate();
            drwNewCopy.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            drawables.add(drwNewCopy);
        }
        if (!clockwise) {
            Collections.reverse(drawables);
        }
        for (Drawable drawable: drawables) {
            animation.addFrame(drawable, duration);
        }
        return animation;
    }

}
