package iosdialog;

import android.graphics.drawable.GradientDrawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.UiThread;

import iosdialog.utils.DialogUtils;
import ru.crmkurgan.main.R;

public class DialogInit {


    @LayoutRes
    static int getInflateLayout(IOSDialog.Builder builder) {
        if (builder.customView != null) {
            return -1;
        } else {
            return R.layout.ios_progress_dialog;
        }
    }


    @UiThread
    public static void init(final IOSDialog dialog) {
        final IOSDialog.Builder builder = dialog.builder;

        dialog.titleFrame = (LinearLayout) dialog.rootView.findViewById(R.id.title_frame);
        dialog.titleIcon = (ImageView) dialog.rootView.findViewById(R.id.title_icon);
        dialog.title = (TextView) dialog.rootView.findViewById(R.id.title_text);
        dialog.spinner = (CamomileSpinner) dialog.rootView.findViewById(R.id.spinner);
        dialog.message = (TextView) dialog.rootView.findViewById(R.id.message);

        dialog.setCancelable(builder.cancelable);
        if (builder.isBackgroundColorSet) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(
                    builder.context.getResources().getDimension(R.dimen.ios_bg_corner_radius)
            );
            drawable.setColor(builder.backgroundColor);
            dialog.getWindow().setBackgroundDrawable(drawable);
        } else {
            builder.backgroundColor = DialogUtils
                    .getColor(builder.context, R.color.dark_gray_background);
        }

        if (!builder.isTitleColorSet) {
            builder.titleColor = DialogUtils
                    .getColor(builder.context, R.color.standard_white);
        }
        if (!builder.isMessageColorSet) {
            builder.messageColor = DialogUtils
                    .getColor(builder.context, R.color.standard_white);
        }

        setupTitle(dialog, builder);
        setupSpinner(dialog, builder);
        setupMessageContent(dialog, builder);
        setupListeners(dialog, builder);


        dialog.setContentView(dialog.rootView);
    }

    private static void setupTitle(IOSDialog dialog, IOSDialog.Builder builder) {
        if (dialog.title != null) {
            dialog.title.setTextColor(builder.titleColor);
            dialog.title.setGravity(builder.titleGravity);
            dialog.title.setTextAlignment(builder.titleGravity);

            if (builder.title == null) {
                dialog.titleFrame.setVisibility(View.GONE);
            } else {
                dialog.title.setText(builder.title);
                dialog.titleFrame.setVisibility(View.VISIBLE);
            }
        }
    }

    private static void setupSpinner(IOSDialog dialog, IOSDialog.Builder builder) {
        if (builder.spinnerColor == 0) builder.spinnerColor = CamomileSpinner.DEFAULT_COLOR;
        if (builder.spinnerDuration == 0) builder.spinnerDuration = CamomileSpinner.DEFAULT_DURATION;
        if (builder.oneShot) dialog.spinner.setOneShot(true);
        dialog.spinner.recreateWithParams(
                builder.context,
                builder.spinnerColor,
                builder.spinnerDuration,
                builder.spinnerClockwise
        );
    }

    private static void setupListeners(IOSDialog dialog, IOSDialog.Builder builder) {
        if (builder.showListener != null) {
            dialog.setOnShowListener(builder.showListener);
        }
        if (builder.cancelListener != null) {
            dialog.setOnCancelListener(builder.cancelListener);
        }
        if (builder.dismissListener != null) {
            dialog.setOnDismissListener(builder.dismissListener);
        }
        if (builder.keyListener != null) {
            dialog.setOnKeyListener(builder.keyListener);
        }
    }

    private static void setupMessageContent(IOSDialog dialog, IOSDialog.Builder builder) {
        if (dialog.message != null) {
            dialog.message.setMovementMethod(new LinkMovementMethod());
            dialog.message.setTextColor(builder.messageColor);
            dialog.message.setGravity(builder.messageGravity);
            dialog.message.setTextAlignment(builder.messageGravity);

            if (builder.message != null) {
                dialog.message.setText(builder.message);
                dialog.message.setVisibility(View.VISIBLE);
            } else {
                dialog.message.setVisibility(View.GONE);
            }
        }
    }
}
