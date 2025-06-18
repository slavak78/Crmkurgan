package ru.crmkurgan.main.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
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

import ru.crmkurgan.main.Activity.FilterSearchActivity;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Item.CategoryItem;
import ru.crmkurgan.main.Item.ListItem;
import ru.crmkurgan.main.Models.CategoryModels;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.Models.RaionModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class PropertyFragment extends Fragment {
    View getView;
    Context context;
    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    String valuemin, valuemax, sortdata, value1min, value1max;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    ArrayList<String> catNameList;
    ArrayList<CategoryModels> mListCat;
    ImageView search, backbtn;
    LinearLayout llfilter, llsort, noresult;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    int save_sort=1;
    Toolbar toolbar;

    public ArrayList<CategoryModels> tosend;
    String valueraion = "";
    ArrayList<String> raionNameList;
    ArrayList<RaionModels> mListRaion;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycle, container, false);
        context = getContext();
        View bottom_sheet = getView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        listItem = new ArrayList<>();
        mListCat= new ArrayList<>();
        catNameList= new ArrayList<>();
        raionNameList= new ArrayList<>();
        mListRaion= new ArrayList<>();
        tosend = new ArrayList<>();
        recyclerView = getView.findViewById(R.id.recycle);
        filterandsort = getView.findViewById(R.id.rlfilter);
        notFound = getView.findViewById(R.id.notfound);
        progress = getView.findViewById(R.id.progress);
        backbtn = getView.findViewById(R.id.back_btn);
        search = getView.findViewById(R.id.search);
        llfilter = getView.findViewById(R.id.llfilter);
        llsort = getView.findViewById(R.id.llsort);
        toolbar = getView.findViewById(R.id.toolbar);
        noresult = getView.findViewById(R.id.noresult);
        toolbar.setVisibility(View.GONE);

        backbtn.setOnClickListener(v -> requireActivity().finish());

        progress.setVisibility(View.VISIBLE);
        llsort.setOnClickListener(v -> sort());
        llfilter.setOnClickListener(v -> searchFilter());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        getData();
        getCategory();
        getRaion();
        return getView;
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
                if (NetworkUtils.isConnected(context)) {
                    JSONObject parameters = new JSONObject();
                    RequestQueue rq = Volley.newRequestQueue(context);
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
                if (NetworkUtils.isConnected(context)) {
                    JSONObject parameters = new JSONObject();
                    RequestQueue rq = Volley.newRequestQueue(context);
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
                if (NetworkUtils.isConnected(context)) {
                    getData();
                }}

        });

        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setContentView(mDialog);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    private void searchFilter() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final RangeSeekBar seekbar = dialog.findViewById(R.id.seekbar);
        final RangeSeekBar seekbar1 = dialog.findViewById(R.id.seekbar1);
        final TextView min = dialog.findViewById(R.id.textmin);
        final TextView max = dialog.findViewById(R.id.textmax);
        final TextView areamin = dialog.findViewById(R.id.areamin);
        final TextView areamax = dialog.findViewById(R.id.areamax);
        final Button submit = dialog.findViewById(R.id.submit);
        final ImageView close = dialog.findViewById(R.id.bt_close);
        final RecyclerView catRecyclerView = dialog.findViewById(R.id.catrecyclerview);
        catRecyclerView.setHasFixedSize(true);
        catRecyclerView.setNestedScrollingEnabled(false);
        catRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        final CategoryItem catadapter = new CategoryItem(context, mListCat, R.layout.item_list_cat);
        catRecyclerView.setAdapter(catadapter);

        final Spinner raion = (Spinner) dialog.findViewById(R.id.raion);
        ArrayAdapter<String> raionSpinner = new ArrayAdapter<>(context, R.layout.spinner, raionNameList);
        raionSpinner.setDropDownViewResource(R.layout.spinner);
        raion.setAdapter(raionSpinner);
        raion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    valueraion = mListRaion.get(raion.getSelectedItemPosition()).getRaionName();
                } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    valueraion = mListRaion.get(raion.getSelectedItemPosition()).getRaionName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        valuemin = "0";
        valuemax = "10000000";
        value1min = "0";
        value1max = "3000";

        min.setText((context.getResources().getString(R.string.min)));
        max.setText(context.getResources().getString(R.string.max));
        areamin.setText(Html.fromHtml("0 м<sup><small><small>2</small></small></sup>"));
        areamax.setText(Html.fromHtml("3000 м<sup><small><small>2</small></small></sup>"));

        seekbar.setRange(0,10000000);
        seekbar.setValue(0,10000000);

        seekbar1.setRange(0,3000);
        seekbar1.setValue(0,3000);

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
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });


        seekbar1.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(' ');
                DecimalFormat df = new DecimalFormat();
                df.setDecimalFormatSymbols(symbols);
                df.setGroupingSize(3);
                String min1 = df.format(Math.round(leftValue));
                min1 = min1 + " м<sup><small><small>2</small></small></sup>";
                areamin.setText(Html.fromHtml(min1));
                String max1 = df.format(Math.round(rightValue));
                max1 = max1 + " м<sup><small><small>2</small></small></sup>";
                areamax.setText(Html.fromHtml(max1));
                value1min = String.valueOf(leftValue);
                value1max =String.valueOf(rightValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });

        close.setOnClickListener(v -> dialog.dismiss());


        submit.setOnClickListener(view -> {
            Intent intent = new Intent(context, FilterSearchActivity.class);
            intent.putExtra("pricemin", valuemin);
            intent.putExtra("pricemax", valuemax);
            intent.putExtra("valueraion", valueraion);
            intent.putExtra("areamin", value1min);
            intent.putExtra("areamax", value1max);
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
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.ALLPROPERTY, parameters, response -> {
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
                }
                if(listItem.isEmpty()) {
                    noresult.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        adapter = new ListItem(context, listItem, R.layout.item_list);
        recyclerView.setAdapter(adapter);
    }

    private void getCategory() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(context);
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
                    item.setCategoryTabl(userdata.getString("tabl"));
                    item.setCategoryName(userdata.getString("cname"));
                    item.setCategorySelected(1);
                    mListCat.add(item);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    private void getRaion() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.RAION, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataRaion(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataRaion(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    RaionModels item = new RaionModels();
                    item.setRaionId(userdata.getString("id"));
                    item.setRaionName(userdata.getString("name"));
                    raionNameList.add(userdata.getString("name"));
                    mListRaion.add(item);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


}
