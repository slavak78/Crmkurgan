package ru.crmkurgan.main.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

public class AboutActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    WebView webView;
    AboutModels modelAbout;
    ImageView web, phone, email;
    RelativeLayout nointernet;
    ImageView backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        webView = findViewById(R.id.webView);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        web = findViewById(R.id.web);
        nointernet = findViewById(R.id.nointernet);
        modelAbout = new AboutModels();
        backbtn = findViewById(R.id.back_btn);
        mProgressBar = findViewById(R.id.progressBar);
        webView.setBackgroundColor(Color.TRANSPARENT);
        if (NetworkUtils.isConnected(AboutActivity.this)) {
            getAboutUs();
        } else {
            nointernet.setVisibility(View.VISIBLE);
        }

        phone.setOnClickListener(view -> {
            final int REQUEST_PHONE_CALL = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    call();
                }
            } else {
                call();
            }
        });

        backbtn.setOnClickListener(v -> finish());

        email.setOnClickListener(v -> sendMail());

        web.setOnClickListener(v -> {
            String url = "https://"+(modelAbout.getAppWebsite());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    private void getAboutUs() {
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

    public void call() {
        if(ContextCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + (modelAbout.getAppContact())));
            startActivity(callIntent);
        }
    }

    public void getData(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);

                modelAbout.setAppContact(userdata.getString("app_contact"));
                modelAbout.setAppEmail(userdata.getString("app_email"));
                modelAbout.setAppWebsite(userdata.getString("app_website"));
                modelAbout.setAppDescription(userdata.getString("app_description"));

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        setResult();
    }

    private void setResult() {

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = modelAbout.getAppDescription();
        String text = "<html dir=" + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #a5a5a5;text-align:left;line-height:1.2}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
    }


    public void showToast(String msg) {
        Toast.makeText(AboutActivity.this, msg, Toast.LENGTH_LONG).show();
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

    @SuppressLint("IntentReset")
    private void sendMail() {
        Log.i("Send email", "");

        String[] TO = {(modelAbout.getAppEmail())};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text)+"\n");
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.sende)));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AboutActivity.this,
                    getString(R.string.noemail), Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        boolean permissionGranted = false;
        if (requestCode == 1) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if(permissionGranted) {
            call();
        }
    }
}
