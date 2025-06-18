package httpclient.request;

import java.io.File;

public abstract class FileUploadListener {


    public abstract void onUploadingFile(File file, long size, long uploaded);

    public void onUploadStart() {

    }

    public void onUploadCancel() {

    }

    public void onUploadFinish(File file) {

    }
}
