package ru.crmkurgan.main.Utils;

import java.util.Collections;
import java.util.List;

public class ImageProvider extends AbstractDataProvider {
    List<DataImage> mUris;


    public ImageProvider(List<DataImage> uris) {
        this.mUris = uris;
    }
    @Override
    public int getCount() {
        return mUris.size();
    }

    @Override
    public Data getItem(int index) {
        if (index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("index = " + index);
        }

        return mUris.get(index);
    }

    @Override
    public void removeItem(int position) {
        mUris.remove(position);
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final DataImage item = mUris.remove(fromPosition);

        mUris.add(toPosition, item);
    }

    @Override
    public void swapItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        Collections.swap(mUris, toPosition, fromPosition);
            }
}
