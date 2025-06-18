package httpclient.response;

import android.util.Log;

import org.jsoup.nodes.Document;


public abstract class OkXmlResponseListener extends XmlResponseListener {

    private static final String TAG = "OkResponseListener";


    @Override
    public void onErrorResponse(Document document) {

    }

    @Override
    public void onParseError(Exception e) {
        Log.e(TAG, "Error", e);
    }
}
