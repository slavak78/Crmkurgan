package httpclient.response;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class OkJsonResponseListener extends JsonResponseListener {

    private static final String TAG = "OkResponseListener";

    @Override
    public void onErrorResponse(JSONObject jsonObject) throws JSONException {

    }

    @Override
    public void onParseError(JSONException e) {
        Log.e(TAG, "Error", e);
    }
}
