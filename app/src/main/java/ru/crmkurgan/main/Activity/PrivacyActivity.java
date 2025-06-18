package ru.crmkurgan.main.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Models.AboutModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    WebView webView;
    AboutModels modelAbout;
    RelativeLayout nointernet;
    ImageView backbtn;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        webView = findViewById(R.id.webView);
        nointernet = findViewById(R.id.nointernet);
        modelAbout = new AboutModels();
        mProgressBar = findViewById(R.id.progressBar);
        backbtn = findViewById(R.id.back_btn);
        webView.setBackgroundColor(Color.TRANSPARENT);
        if (NetworkUtils.isConnected(PrivacyActivity.this)) {
            getPrivacy();
        } else {
            nointernet.setVisibility(View.VISIBLE);
        }
        backbtn.setOnClickListener(v -> finish());

    }

    private void getPrivacy() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SETTINGAPP, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getData(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getData(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);

                modelAbout.setAppPrivaccy(userdata.getString("app_privacy_policy"));

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        setResult();
    }

    private void setResult() {

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = modelAbout.getAppPrivacy();
        String text = "<html dir=" + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #a5a5a5;text-align:left;line-height:1.2}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}

