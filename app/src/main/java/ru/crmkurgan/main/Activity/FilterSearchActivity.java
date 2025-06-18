package ru.crmkurgan.main.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import ru.crmkurgan.main.Models.CategoryModels;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FilterSearchActivity extends AppCompatActivity {

    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    String priceMax, priceMin, json, valueraion, areamin, areamax;
    ImageView backbtn;
    LinearLayout noresult;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    ArrayList<CategoryModels> cm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        cm = new ArrayList<>();
        Intent i = getIntent();
        priceMin = i.getStringExtra("pricemin");
        priceMax = i.getStringExtra("pricemax");
        valueraion = i.getStringExtra("valueraion");
        areamin = i.getStringExtra("areamin");
        areamax = i.getStringExtra("areamax");
        json = i.getStringExtra("json");
        try {
            JSONArray obj = new JSONArray(json);
            for(int i1=0; i1<obj.length(); i1++) {
                JSONObject userdata = obj.getJSONObject(i1);
                if(userdata.getInt("CategorySelected")==1) {
                    CategoryModels item = new CategoryModels();
                    item.setCategoryId(userdata.getString("CategoryId"));
                    item.setCategoryTabl(userdata.getString("CategoryTabl"));
                    item.setCategoryName(userdata.getString("CategoryName"));
                    item.setCategorySelected(userdata.getInt("CategorySelected"));
                    cm.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listItem = new ArrayList<>();
        noresult = findViewById(R.id.noresult);
        recyclerView = findViewById(R.id.recycle);
        filterandsort = findViewById(R.id.rlfilter);
        notFound = findViewById(R.id.notfound);
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        progress.setVisibility(View.VISIBLE);
        filterandsort.setVisibility(View.GONE);

        backbtn.setOnClickListener(v -> finish());
        getData();
    }

    private void getData() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("pricemin", priceMin);
            parameters.put("pricemax", priceMax);
            JSONArray parameters1 = new JSONArray();
            for(int y=0; y<cm.size(); y++) {
                JSONObject parameters2 = new JSONObject();
                parameters2.put("catid", cm.get(y).getCategoryId());
                parameters2.put("tabl", cm.get(y).getCategoryTabl());
                parameters1.put(y, parameters2);
            }
            parameters.put("catids", parameters1);
            parameters.put("valueraion", valueraion);
            parameters.put("areamin", areamin);
            parameters.put("areamax", areamax);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLPROPERTY1, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataAll(respo);
                    progress.setVisibility(View.GONE);
                }, error ->
                        Log.d("respo",error.toString()));
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

                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        adapter = new ListItem(this, listItem, R.layout.item_list);
        recyclerView.setAdapter(adapter);
    }
}
