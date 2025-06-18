package ru.crmkurgan.main.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import ru.crmkurgan.main.Activity.AddPropertyActivity;
import ru.crmkurgan.main.Activity.AllLatestActivity;
import ru.crmkurgan.main.Activity.AllPopularActivity;
import ru.crmkurgan.main.Constants.BaseApp;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Item.GridItem;
import ru.crmkurgan.main.Item.SliderItem;
import ru.crmkurgan.main.Item.TablItem;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.Models.TablModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import me.relex.circleindicator.CircleIndicator;


public class HomeFragment extends Fragment {
    public static SharedPreferences sharedPreferences;
    public static String agent;
    ArrayList<PropertyModels> mSliderList, mLatestList, mPopularList;
    ArrayList<TablModels> mTablList;
    SliderItem sliderItem;
    ScrollView mScrollView;
    ProgressBar mProgressBar, progresspopular, progresslatest;
    ViewPager mViewPager;
    CircleIndicator circleIndicator;
    RecyclerView rvLatest, rvTabl, rvPopular;
    Button addProperty;
    TablItem tablItem;
    TextView moreLatest, morePopular;
    BaseApp baseApp;
    GridItem popularItem, latestItem;
    RelativeLayout nofound,rdd;
    LinearLayout rlslider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mSliderList = new ArrayList<>();
        mLatestList = new ArrayList<>();
        mPopularList = new ArrayList<>();
        mTablList = new ArrayList<>();
        baseApp = BaseApp.getInstance();
        mScrollView = rootView.findViewById(R.id.scrollView);
        mProgressBar = rootView.findViewById(R.id.progressBar);
        progresspopular = rootView.findViewById(R.id.progresspopular);
        progresslatest = rootView.findViewById(R.id.progresslatest);
        mViewPager = rootView.findViewById(R.id.viewPager);
        rlslider = rootView.findViewById(R.id.rlslider);
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
        rvLatest = rootView.findViewById(R.id.latest);
        rvTabl = rootView.findViewById(R.id.category);
        rvPopular = rootView.findViewById(R.id.popular);
        moreLatest = rootView.findViewById(R.id.morelatest);
        morePopular = rootView.findViewById(R.id.morepopular);
        addProperty = rootView.findViewById(R.id.addproperty);
        nofound = rootView.findViewById(R.id.nofound);
        rdd = rootView.findViewById(R.id.rdd);
        progresspopular.setVisibility(View.VISIBLE);
        progresslatest.setVisibility(View.VISIBLE);

        rvLatest.setHasFixedSize(true);
        rvLatest.setNestedScrollingEnabled(false);
        rvLatest.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rvTabl.setHasFixedSize(true);
        rvTabl.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        rvPopular.setHasFixedSize(true);
        rvPopular.setNestedScrollingEnabled(false);
        rvPopular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        if (NetworkUtils.isConnected(getActivity())) {
            getFeaturedItem();
            getTabl();
            getPopularItem();
            getLatestItem();
        } else {
            nofound.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }

        morePopular.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AllPopularActivity.class);
            requireActivity().startActivity(intent);
        });
        moreLatest.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AllLatestActivity.class);
                requireActivity().startActivity(intent);
        });

        sharedPreferences = requireActivity().getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        agent =sharedPreferences.getString("agent","0");
        addProperty.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
                requireActivity().startActivity(intent);
        });


            return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        getParentFragmentManager().beginTransaction().detach(HomeFragment.this).attach(HomeFragment.this).commit();
    }

    private void getFeaturedItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.FEATURED, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getData(respo);
                    mScrollView.setVisibility(View.VISIBLE);
                    addProperty.setVisibility(View.VISIBLE);
                    if (baseApp.getIsLogin()) {
                        if (agent.equals("0")) {
                            addProperty.setVisibility(View.GONE);
                        }
                    } else {
                        addProperty.setVisibility(View.GONE);
                    }
                    mProgressBar.setVisibility(View.GONE);
                    rlslider.setVisibility(View.VISIBLE);
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
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    PropertyModels item = new PropertyModels();
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setAddress(userdata.getString("address"));
                    item.setPrice(userdata.getString("price"));
                    item.setImage(userdata.getString("image"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setTabl(userdata.getString("tabl"));
                    mSliderList.add(item);
                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getTabl() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.TABL, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataTabl(respo);
                    mScrollView.setVisibility(View.VISIBLE);
                    addProperty.setVisibility(View.VISIBLE);
                    if (baseApp.getIsLogin()) {
                        if (agent.equals("0")) {
                            addProperty.setVisibility(View.GONE);
                        }
                    } else {
                        addProperty.setVisibility(View.GONE);
                    }
                    mProgressBar.setVisibility(View.GONE);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataTabl(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    TablModels item = new TablModels();
                    item.setTablId(userdata.getString("cid"));
                    item.setTablName(userdata.getString("cname"));
                    mTablList.add(item);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getPopularItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.POPULAR, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataPopular(respo);
                    addProperty.setVisibility(View.VISIBLE);
                    if (baseApp.getIsLogin()) {
                        if (agent.equals("0")) {
                           addProperty.setVisibility(View.GONE);
                        }
                    } else {
                        addProperty.setVisibility(View.GONE);
                    }
                    mScrollView.setVisibility(View.VISIBLE);
                    progresspopular.setVisibility(View.GONE);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataPopular(String loginData){
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
                    mPopularList.add(item);

                }
                if(msg.length()==0) {
                    rdd.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void getLatestItem() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.LATEST, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataLatest(respo);
                    addProperty.setVisibility(View.VISIBLE);
                    if (baseApp.getIsLogin()) {
                        if (agent.equals("0")) {
                            addProperty.setVisibility(View.GONE);
                        }
                    } else {
                        addProperty.setVisibility(View.GONE);
                    }
                    mScrollView.setVisibility(View.VISIBLE);
                    progresslatest.setVisibility(View.GONE);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataLatest(String loginData){
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
                    item.setPrice(userdata.getString("price"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setTabl(userdata.getString("tabl"));
                    mLatestList.add(item);

                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        displayData();
    }

    private void displayData() {
        sliderItem = new SliderItem(getActivity(), mSliderList);
        mViewPager.setAdapter(sliderItem);
        circleIndicator.setViewPager(mViewPager);

        tablItem = new TablItem(getActivity(), mTablList, R.layout.item_category);
        rvTabl.setAdapter(tablItem);

        popularItem = new GridItem(getActivity(), mPopularList, R.layout.item_grid);
        rvPopular.setAdapter(popularItem);

        latestItem = new GridItem(getActivity(), mLatestList, R.layout.item_grid);
        rvLatest.setAdapter(latestItem);

        if(mSliderList.isEmpty()){
            rlslider.setVisibility(View.GONE);
        }
    }



}