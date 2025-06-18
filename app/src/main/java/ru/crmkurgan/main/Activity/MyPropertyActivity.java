package ru.crmkurgan.main.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Item.ListItem;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyPropertyActivity extends AppCompatActivity {

    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    ImageView backbtn;
    LinearLayout noresult;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    Button addProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        listItem = new ArrayList<>();
        noresult = findViewById(R.id.noresult);
        recyclerView = findViewById(R.id.recycle);
        filterandsort = findViewById(R.id.rlfilter);
        notFound = findViewById(R.id.notfound);
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);
        addProperty = findViewById(R.id.addproperty);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        progress.setVisibility(View.VISIBLE);
        filterandsort.setVisibility(View.GONE);
        addProperty.setVisibility(View.VISIBLE);

        addProperty.setOnClickListener(v -> {
            Intent intent = new Intent(MyPropertyActivity.this, AddPropertyActivity.class);
            startActivity(intent);
        });

        backbtn.setOnClickListener(v -> finish());
        getData();
    }



    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.MYPROPERTY+MainActivity.user_id, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataAll(respo);
                    progress.setVisibility(View.GONE);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataAll(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    PropertyModels item = new PropertyModels();
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setImage(userdata.getString("image"));
                    item.setAddress(userdata.getString("address"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setPrice(userdata.getString("price"));
                    item.setTabl(userdata.getString("tabl"));
                    listItem.add(item);
                    adapter = new ListItem(this, listItem, R.layout.item_list);
                    recyclerView.setAdapter(adapter);
                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }
}
