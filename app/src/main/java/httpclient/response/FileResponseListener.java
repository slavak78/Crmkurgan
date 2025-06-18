package httpclient.response;

import java.io.File;

public abstract class FileResponseListener extends BaseResponseListener {

    private static final String TAG = "FileResponseListener";

    private final File file;

    public FileResponseListener(File file) {
        this.file = file;
    }

    public FileResponseListener(String path) {
        this(new File(path));
    }

    public File getFile() {
        return file;
    }

    @Override
    public void onErrorResponse(int httpCode, String content) {

    }

    @Override
    public void onOkResponse(String content) {
        onOkResponse(file);
    }

    public abstract void onOkResponse(File file);
}
