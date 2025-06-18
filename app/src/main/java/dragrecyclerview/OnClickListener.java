package dragrecyclerview;

import android.view.View;

public interface OnClickListener {
    void onItemClick(View v, int position);

    void onItemLongClick(View v, int position);
}
