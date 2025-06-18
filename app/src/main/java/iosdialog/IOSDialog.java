package iosdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.annotation.UiThread;

import iosdialog.utils.DialogUtils;
import ru.crmkurgan.main.R;

public class IOSDialog extends Dialog {

    protected IOSDialog.Builder builder;
    protected ViewGroup rootView;
    protected LinearLayout titleFrame;
    protected ImageView titleIcon;
    protected TextView title;
    protected CamomileSpinner spinner;
    protected TextView message;

    private IOSDialog(IOSDialog.Builder builder) {
        super(builder.context, builder.theme);
        final LayoutInflater inflater = LayoutInflater.from(builder.context);
        rootView = (ViewGroup) inflater.inflate(DialogInit.getInflateLayout(builder), null);
        this.builder = builder;
        DialogInit.init(this);
    }

    private IOSDialog(Context context, int theme) {
        super(context, theme);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        AnimationDrawable spinnerAnimation = (AnimationDrawable) spinner.getBackground();
        spinnerAnimation.start();
    }

    public static class Builder {

        protected Context context;
        protected View customView;
        protected CharSequence title;
        protected CharSequence message;
        protected boolean cancelable;
        protected boolean indeterminate;
        protected float dimAmount;
        protected int spinnerDuration;

        protected int titleGravity;
        protected int messageGravity;

        protected OnShowListener showListener;
        protected OnCancelListener cancelListener;
        protected OnDismissListener dismissListener;
        protected OnKeyListener keyListener;

        protected Typeface regularFont;
        protected Typeface mediumFont;

        protected int theme;

        protected int titleColor;
        protected int messageColor;
        protected int spinnerColor;
        protected int backgroundColor;

        protected boolean spinnerClockwise = true;

        protected boolean oneShot = false;

        protected boolean isTitleColorSet = false;
        protected boolean isMessageColorSet = false;
        protected boolean isSpinnerColorSet = false;
        protected boolean isBackgroundColorSet = false;

        public Builder(Context context) {
            this.context = context;
            this.theme = R.style.CamomileDialog;
            this.cancelable = true;
            this.titleGravity = Gravity.CENTER;
            this.messageGravity = Gravity.CENTER;
            this.dimAmount = 0.2f;

            if (this.mediumFont == null) {
                try {
                    this.mediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL);
                } catch (Exception ignored) {
                    this.mediumFont = Typeface.DEFAULT_BOLD;
                }
            }
            if (this.regularFont == null) {
                try {
                    this.regularFont = Typeface.create("sans-serif", Typeface.NORMAL);
                } catch (Exception ignored) {
                    this.regularFont = Typeface.SANS_SERIF;
                    if (this.regularFont == null) {
                        this.regularFont = Typeface.DEFAULT;
                    }
                }
            }
        }

        public Builder setTitle(@StringRes int titleRes) {
            setTitle(this.context.getText(titleRes));
            return this;
        }

        public void setTitle(@NonNull CharSequence title) {
            this.title = title;
        }

        public Builder setTitleGravity(int gravity) {
            this.titleGravity = gravity;
            return this;
        }

        public Builder setMessageContent(@StringRes int messageContent) {
            return setMessageContent(messageContent, false);
        }

        public Builder setMessageContent(@StringRes int messageContentRes, boolean html) {
            CharSequence text = this.context.getText(messageContentRes);
            if (html) {
                text = Html.fromHtml(text.toString().replace("\n", "<br/>"));
            }
            return setMessageContent(text);
        }

        public Builder setMessageContent(@StringRes int messageContentRes, Object... formatArgs) {
            String str = String.format(this.context.getString(messageContentRes), formatArgs)
                    .replace("\n", "<br/>");
            return setMessageContent(Html.fromHtml(str));
        }

        public Builder setMessageContent(@NonNull CharSequence messageContent) {
            if (this.customView != null) {
                throw new IllegalStateException("You cannot setMessageContent() " +
                        "when you're using a custom view.");
            }
            this.message = messageContent;
            return this;
        }

        public Builder setMessageContentGravity(int gravity) {
            this.messageGravity = gravity;
            return this;
        }

        public Builder setMessageColor(@ColorInt int color) {
            this.messageColor = color;
            this.isMessageColorSet = true;
            return this;
        }

        public Builder setMessageColorRes(@ColorRes int colorRes) {
            return setMessageColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder setMessageColorAttr(@AttrRes int colorAttr) {
            return setMessageColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(@NonNull OnCancelListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setOnShowListener(@NonNull OnShowListener showListener) {
            this.showListener = showListener;
            return this;
        }

        public Builder setOnDismissListener(@NonNull OnDismissListener dismissListener) {
            this.dismissListener = dismissListener;
            return this;
        }

        public Builder setOnKeyListener(@NonNull OnKeyListener keyListener) {
            this.keyListener = keyListener;
            return this;
        }
        @Deprecated
        public Builder setIndeterminate(boolean indeterminate) {
            this.indeterminate = indeterminate;
            return this;
        }

        @Deprecated
        public Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }

        public Builder setSpinnerColor(@ColorInt int color) {
            this.spinnerColor = color;
            this.isSpinnerColorSet = true;
            return this;
        }

        public Builder setSpinnerColorRes(@ColorRes int colorRes) {
            return setSpinnerColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder setSpinnerColorAttr(@AttrRes int colorAttr) {
            return setSpinnerColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder setSpinnerDuration(int spinnerDuration) {
            this.spinnerDuration = spinnerDuration;
            return this;
        }

        public Builder setSpinnerClockwise(boolean spinnerClockwise) {
            this.spinnerClockwise = spinnerClockwise;
            return this;
        }

        public Builder setOneShot(boolean oneShot) {
            this.oneShot = oneShot;
            return this;
        }

        public Builder setTitleColor(@ColorInt int color) {
            this.titleColor = color;
            this.isTitleColorSet = true;
            return this;
        }

        public Builder setTitleColorRes(@ColorRes int colorRes) {
            return setTitleColor(DialogUtils.getColor(this.context, colorRes));
        }

        public Builder setTitleColorAttr(@AttrRes int colorAttr) {
            return setTitleColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        @Deprecated
        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            this.isBackgroundColorSet = true;
            return this;
        }
        @Deprecated
        public Builder setBackgroundColorRes(@ColorRes int colorRes) {
            return setBackgroundColor(DialogUtils.getColor(this.context, colorRes));
        }

        @Deprecated
        public Builder setBackgroundColorAttr(@AttrRes int colorAttr) {
            return setBackgroundColor(DialogUtils.resolveColor(this.context, colorAttr));
        }

        public Builder setTheme(@StyleRes int theme) {
            this.theme = theme;
            return this;
        }

        @UiThread
        public IOSDialog build() {
            return new IOSDialog(this);
        }

        @UiThread
        public IOSDialog show() {
            IOSDialog dialog = build();
            dialog.show();
            return dialog;
        }

    }

    private Drawable adjust(Drawable d) {
        int to = Color.RED;

        Bitmap src = ((BitmapDrawable) d).getBitmap();
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
        for(int x = 0;x < bitmap.getWidth();x++)
            for(int y = 0;y < bitmap.getHeight();y++)
                if(match(bitmap.getPixel(x, y)))
                    bitmap.setPixel(x, y, to);

        return new BitmapDrawable(this.builder.context.getResources(), bitmap);
    }

    private Bitmap adjust(Bitmap src) {
        int to = Color.RED;

        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
        for(int x = 0;x < bitmap.getWidth();x++)
            for(int y = 0;y < bitmap.getHeight();y++)
                if(match(bitmap.getPixel(x, y)))
                    bitmap.setPixel(x, y, to);

        return bitmap;
    }

    private static final int[] FROM_COLOR = new int[]{49, 179, 110};
    private static final int THRESHOLD = 3;
    private boolean match(int pixel) {
        return Math.abs(Color.red(pixel) - FROM_COLOR[0]) < THRESHOLD &&
                Math.abs(Color.green(pixel) - FROM_COLOR[1]) < THRESHOLD &&
                Math.abs(Color.blue(pixel) - FROM_COLOR[2]) < THRESHOLD;
    }

}
