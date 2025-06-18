package httpclient.response;

import java.io.File;

public abstract class FileDownloadListener {

    static final String TAG = "FileDownloadListener";

    public abstract void onDownloadingFile(File file, long size, long downloaded);

    public void onDownloadStart() {

    }

    public void onDownloadCancel() {

    }

    public void onDownloadFinish() {

    }
}
