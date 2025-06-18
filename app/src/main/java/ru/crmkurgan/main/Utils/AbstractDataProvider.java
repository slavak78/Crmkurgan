package ru.crmkurgan.main.Utils;
import android.net.Uri;


public abstract class AbstractDataProvider {

    public static abstract class Data {
        public abstract long getId();


        public abstract int getViewType();

        public abstract Uri getUri();

        public abstract void setPinned(boolean pinned);

        public abstract boolean isPinned();
    }

    public abstract int getCount();

    public abstract Data getItem(int index);

    public abstract void removeItem(int position);

    public abstract void moveItem(int fromPosition, int toPosition);

    public abstract void swapItem(int fromPosition, int toPosition);

}
