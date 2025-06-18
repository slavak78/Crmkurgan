package httpclient.request;

public interface RequestStateListener {

    void onStart();
    void onFinish();
    void onConnectionError(Exception e);
}
