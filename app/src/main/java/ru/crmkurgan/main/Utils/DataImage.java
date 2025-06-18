package ru.crmkurgan.main.Utils;

import android.net.Uri;

import java.util.Comparator;

public class DataImage extends AbstractDataProvider.Data {
    Uri uris;
    long j;
    int mViewType;

    public DataImage(long j, int i, Uri uris) {
        this.uris = uris;
        this.j = j;
        this.mViewType = i;
    }
    @Override
    public long getId() {
        return j;
    }

    @Override
    public int getViewType()
    {
        return mViewType;
    }

    @Override
    public Uri getUri() {
        return uris;
    }

    @Override
    public void setPinned(boolean pinned) {

    }

    @Override
    public boolean isPinned() {
        return false;
    }

    public static final Comparator<DataImage> COMPARE_BY_J = (lhs, rhs) -> (int) (lhs.getId() - rhs.getId());
}
