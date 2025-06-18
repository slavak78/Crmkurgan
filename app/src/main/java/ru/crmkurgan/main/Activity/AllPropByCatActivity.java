package ru.crmkurgan.main.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Item.CategoryItem;
import ru.crmkurgan.main.Item.ListItem;
import ru.crmkurgan.main.Models.CategoryModels;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;


public class AllPropByCatActivity extends AppCompatActivity {

    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    String valuemin, valuemax, sortdata, Id;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    ArrayList<String> catNameList, cityNameList;
    ArrayList<CategoryModels> mListCat;
    ImageView search, backbtn;
    LinearLayout llfilter, llsort, noresult;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    int save_sort=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycle);
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        View bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        listItem = new ArrayList<>();
        mListCat= new ArrayList<>();
        cityNameList= new ArrayList<>();
        catNameList= new ArrayList<>();
        recyclerView = findViewById(R.id.recycle);
        filterandsort = findViewById(R.id.rlfilter);
        notFound = findViewById(R.id.notfound);
        progress = findViewById(R.id.progress);
        backbtn = findViewById(R.id.back_btn);
        search = findViewById(R.id.search);
        llfilter = findViewById(R.id.llfilter);
        llsort = findViewById(R.id.llsort);
        noresult = findViewById(R.id.noresult);

        backbtn.setOnClickListener(v -> finish());

        progress.setVisibility(View.VISIBLE);
        llsort.setOnClickListener(v -> sort());
        llfilter.setOnClickListener(v -> searchFilter());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        getData();
        getCategory();
    }

    private void sort() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_list, null);
        RadioGroup radioGroupSort = mDialog.findViewById(R.id.myRadioGroup);
        RadioButton filter_low = mDialog.findViewById(R.id.sort_law);
        RadioButton filter_high = mDialog.findViewById(R.id.sort_high);
        RadioButton filter_all=mDialog.findViewById(R.id.sort_all);

        if(save_sort==1) {
            filter_all.setChecked(true);
        }else if(save_sort==2){
            filter_high.setChecked(true);
        }else if(save_sort==3){
            filter_low.setChecked(true);
        }
        sortdata = "DESC";
        radioGroupSort.setOnCheckedChangeListener((radioGroup, Check) -> {
            listItem.clear();
            if (Check == R.id.sort_high) {
                save_sort=2;
                if (NetworkUtils.isConnected(AllPropByCatActivity.this)) {
                    JSONObject parameters = new JSONObject();
                    RequestQueue rq = Volley.newRequestQueue(AllPropByCatActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, Constants.FILTERPRICE+"DESC", parameters, response -> {
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
            } else if (Check == R.id.sort_law) {
                save_sort=3;
                if (NetworkUtils.isConnected(AllPropByCatActivity.this)) {
                    JSONObject parameters = new JSONObject();
                    RequestQueue rq = Volley.newRequestQueue(AllPropByCatActivity.this);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.POST, Constants.FILTERPRICE+"ASC", parameters, response -> {
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
            }
            else if (Check == R.id.sort_all) {
                save_sort=1;
                sortdata = ("Sort All");
                if (NetworkUtils.isConnected(AllPropByCatActivity.this)) {
                    getData();
                }}

        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(mDialog);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    private void searchFilter() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_filter);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final RangeSeekBar seekbar = (RangeSeekBar) dialog.findViewById(R.id.seekbar);
        final TextView min = (TextView) dialog.findViewById(R.id.textmin);
        final TextView max = (TextView) dialog.findViewById(R.id.textmax);
        final Button submit = (Button) dialog.findViewById(R.id.submit);
        final ImageView close = (ImageView) dialog.findViewById(R.id.bt_close);

        final RecyclerView catRecyclerView = dialog.findViewById(R.id.catrecyclerview);
        catRecyclerView.setHasFixedSize(true);
        catRecyclerView.setNestedScrollingEnabled(false);
        catRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        final CategoryItem catadapter = new CategoryItem(this, mListCat, R.layout.item_list_cat);
        catRecyclerView.setAdapter(catadapter);

        valuemin = "0";
        valuemax = "10000000";

        min.setText(getResources().getString(R.string.min));
        max.setText(getResources().getString(R.string.max));
        seekbar.setValue(0,10000000);

        seekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(' ');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                String min1 = df.format(Math.round(leftValue));
                min1 = min1 + " ₽";
                min.setText(min1);
                String max1 = df.format(Math.round(rightValue));
                max1 = max1 + " ₽";
                max.setText(max1);
                valuemin = String.valueOf(leftValue);
                valuemax =String.valueOf(rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //do what you want!!
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //do what you want!!
            }
        });

        close.setOnClickListener(v -> dialog.dismiss());

        submit.setOnClickListener(view -> {
            Intent intent = new Intent(AllPropByCatActivity.this, FilterSearchActivity.class);
            intent.putExtra("pricemin", valuemin);
            intent.putExtra("pricemax", valuemax);
            Gson gson = new Gson();
            String json = gson.toJson(mListCat);
            intent.putExtra("json", json);
            startActivity(intent);
            dialog.dismiss();


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.BYCAT+Id, parameters, response -> {
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

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CATEGORY, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataCategory(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataCategory(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    CategoryModels item = new CategoryModels();
                    item.setCategoryId(userdata.getString("cid"));
                    item.setCategoryName(userdata.getString("cname"));
                    item.setCategorySelected(1);
                    catNameList.add(userdata.getString("cname"));
                    mListCat.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
}
