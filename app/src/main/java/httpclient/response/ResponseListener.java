package httpclient.response;

public interface ResponseListener {

    void onResponse(int httpCode, String content);
}
