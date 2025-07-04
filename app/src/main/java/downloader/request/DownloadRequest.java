package downloader.request;

import downloader.Error;
import downloader.OnCancelListener;
import downloader.OnDownloadListener;
import downloader.OnPauseListener;
import downloader.OnProgressListener;
import downloader.OnStartOrResumeListener;
import downloader.Priority;
import downloader.Response;
import downloader.Status;
import downloader.core.Core;
import downloader.internal.ComponentHolder;
import downloader.internal.DownloadRequestQueue;
import downloader.internal.SynchronousCall;
import downloader.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

public class DownloadRequest {

    private Priority priority;
    private Object tag;
    private String url;
    private String dirPath;
    private String fileName;
    private int sequenceNumber;
    private Future future;
    private long downloadedBytes;
    private long totalBytes;
    private int readTimeout;
    private int connectTimeout;
    private String userAgent;
    private OnProgressListener onProgressListener;
    private OnDownloadListener onDownloadListener;
    private OnStartOrResumeListener onStartOrResumeListener;
    private OnPauseListener onPauseListener;
    private OnCancelListener onCancelListener;
    private int downloadId;
    private final HashMap<String, List<String>> headerMap;
    private Status status;

    DownloadRequest(DownloadRequestBuilder builder) {
        this.url = builder.url;
        this.dirPath = builder.dirPath;
        this.fileName = builder.fileName;
        this.headerMap = builder.headerMap;
        this.priority = builder.priority;
        this.tag = builder.tag;
        this.readTimeout =
                builder.readTimeout != 0 ?
                        builder.readTimeout :
                        getReadTimeoutFromConfig();
        this.connectTimeout =
                builder.connectTimeout != 0 ?
                        builder.connectTimeout :
                        getConnectTimeoutFromConfig();
        this.userAgent = builder.userAgent;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public HashMap<String, List<String>> getHeaders() {
        return headerMap;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getUserAgent() {
        if (userAgent == null) {
            userAgent = ComponentHolder.getInstance().getUserAgent();
        }
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OnProgressListener getOnProgressListener() {
        return onProgressListener;
    }

    public DownloadRequest setOnStartOrResumeListener(OnStartOrResumeListener onStartOrResumeListener) {
        this.onStartOrResumeListener = onStartOrResumeListener;
        return this;
    }

    public DownloadRequest setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
        return this;
    }

    public DownloadRequest setOnPauseListener(OnPauseListener onPauseListener) {
        this.onPauseListener = onPauseListener;
        return this;
    }

    public DownloadRequest setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public int start(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
        downloadId = Utils.getUniqueId(url, dirPath, fileName);
        DownloadRequestQueue.getInstance().addRequest(this);
        return downloadId;
    }

    public Response executeSync() {
        downloadId = Utils.getUniqueId(url, dirPath, fileName);
        return new SynchronousCall(this).execute();
    }

    public void deliverError(final Error error) {
        if (status != Status.CANCELLED) {
            setStatus(Status.FAILED);
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(() -> {
                        if (onDownloadListener != null) {
                            onDownloadListener.onError(error);
                        }
                        finish();
                    });
        }
    }

    public void deliverSuccess() {
        if (status != Status.CANCELLED) {
            setStatus(Status.COMPLETED);
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(() -> {
                        if (onDownloadListener != null) {
                            onDownloadListener.onDownloadComplete();
                        }
                        finish();
                    });
        }
    }

    public void deliverStartEvent() {
        if (status != Status.CANCELLED) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(() -> {
                        if (onStartOrResumeListener != null) {
                            onStartOrResumeListener.onStartOrResume();
                        }
                    });
        }
    }

    public void deliverPauseEvent() {
        if (status != Status.CANCELLED) {
            Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                    .execute(new Runnable() {
                        public void run() {
                            if (onPauseListener != null) {
                                onPauseListener.onPause();
                            }
                        }
                    });
        }
    }

    private void deliverCancelEvent() {
        Core.getInstance().getExecutorSupplier().forMainThreadTasks()
                .execute(() -> {
                    if (onCancelListener != null) {
                        onCancelListener.onCancel();
                    }
                });
    }

    public void cancel() {
        status = Status.CANCELLED;
        if (future != null) {
            future.cancel(true);
        }
        deliverCancelEvent();
        Utils.deleteTempFileAndDatabaseEntryInBackground(Utils.getTempPath(dirPath, fileName), downloadId);
    }

    private void finish() {
        destroy();
        DownloadRequestQueue.getInstance().finish(this);
    }

    private void destroy() {
        this.onProgressListener = null;
        this.onDownloadListener = null;
        this.onStartOrResumeListener = null;
        this.onPauseListener = null;
        this.onCancelListener = null;
    }

    private int getReadTimeoutFromConfig() {
        return ComponentHolder.getInstance().getReadTimeout();
    }

    private int getConnectTimeoutFromConfig() {
        return ComponentHolder.getInstance().getConnectTimeout();
    }

}
