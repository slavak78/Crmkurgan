package ru.crmkurgan.main.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;
import imagepicker.ui.imagepicker.ImagePicker;

import httpclient.debug.Logger;
import httpclient.request.FileUploadListener;
import httpclient.request.RequestStateListener;
import httpclient.request.RequestHttp;
import httpclient.response.JsonResponseListener;

import imagepicker.model.Image;
import imagepicker.widget.GridSpacingItemDecoration;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Models.BalkonModels;
import ru.crmkurgan.main.Models.CategoryModels;
import ru.crmkurgan.main.Models.ClientModels;
import ru.crmkurgan.main.Models.GodaModels;
import ru.crmkurgan.main.Models.LiftModels;
import ru.crmkurgan.main.Models.MebelModels;
import ru.crmkurgan.main.Models.PereplanModels;
import ru.crmkurgan.main.Models.PlanirModels;
import ru.crmkurgan.main.Models.PostroyModels;
import ru.crmkurgan.main.Models.RemontModels;
import ru.crmkurgan.main.Models.SanuzelModels;
import ru.crmkurgan.main.Models.StekloModels;
import ru.crmkurgan.main.Models.StenaModels;
import ru.crmkurgan.main.Models.UslovieModels;
import ru.crmkurgan.main.Models.WindModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.AbstractDataProvider;
import ru.crmkurgan.main.Utils.DataImage;
import ru.crmkurgan.main.Utils.DraggableAdapter;
import ru.crmkurgan.main.Utils.ImageProvider;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddPropertyActivity extends AppCompatActivity {
    EditText area, description, price, pl_zhil, pl_kitch, nach_etash, kon_etash, ceiling, obmen,
    sdelka, video, cadast, razd_komnat, apart;
    private LatLng destinationLocation;
    TextView address,data_sozd,data_do;
    private BottomSheetDialog mBottomSheetDialog;
    LinearLayout lladdress, lldata_sozd, lldata_do;
    Button submit, viewvideo;
    ArrayList<String>catNameList;
    ArrayList<CategoryModels> categoriList;
    RadioButton exs, sogl, info;
    CheckBox online, yandex, cian, domclick;

    ArrayList<String>clientNameList;
    ArrayList<ClientModels> clientList;

    ArrayList<String>balkonNameList;
    ArrayList<BalkonModels> balkonList;

    ArrayList<String>planirNameList;
    ArrayList<PlanirModels> planirList;

    ArrayList<String>sanuzelNameList;
    ArrayList<SanuzelModels> sanuzelList;

    ArrayList<String>stekloNameList;
    ArrayList<StekloModels> stekloList;

    ArrayList<String>liftNameList;
    ArrayList<LiftModels> liftList;

    ArrayList<String>godaNameList;
    ArrayList<GodaModels> godaList;

    ArrayList<String>mebelNameList;
    ArrayList<MebelModels> mebelList;

    ArrayList<String>stenaNameList;
    ArrayList<StenaModels> stenaList;

    ArrayList<String>pereplanNameList;
    ArrayList<PereplanModels> pereplanList;

    ArrayList<String>postroyNameList;
    ArrayList<PostroyModels> postroyList;

    ArrayList<String>remontNameList;
    ArrayList<RemontModels> remontList;

    ArrayList<String>windNameList;
    ArrayList<WindModels> windList;

    ArrayList<String>uslovieNameList;
    ArrayList<UslovieModels> uslovieList;

    String message;
    boolean isgallery = false;
    private final int DESTINATION_ID = 1;
    Button gallery;
    ImageView backbtn;
    Spinner category, client, balkon, planir, sanuzel, steklo, lift, goda, mebel, stena, pereplan,
            postroy, remont, wind, uslovie;
    ProgressDialog dialog;
    ActivityResultLauncher<Intent> ResultLauncher;
    YouTubePlayer youTubePlayer;
    YouTubePlayerSupportFragmentX youTubePlayerFragment;
    RecyclerView osnovafoto;

    private RecyclerView.LayoutManager mLayoutManager;
    private DraggableAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;
    List<DataImage> k = new ArrayList<>();
    private AbstractDataProvider mDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        mLayoutManager = new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false);
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        mRecyclerViewDragDropManager.setDragStartItemAnimationDuration(250);
        mRecyclerViewDragDropManager.setDraggingItemAlpha(0.8f);
        mRecyclerViewDragDropManager.setDraggingItemScale(1.3f);
        mRecyclerViewDragDropManager.setDraggingItemRotation(15.0f);
        mRecyclerViewDragDropManager.setItemMoveMode(RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT);

        dialog = new ProgressDialog(AddPropertyActivity.this, R.style.AlertDialog);
        categoriList = new ArrayList<>();
        catNameList = new ArrayList<>();

        clientList = new ArrayList<>();
        clientNameList = new ArrayList<>();

        balkonList = new ArrayList<>();
        balkonNameList = new ArrayList<>();

        planirList = new ArrayList<>();
        planirNameList = new ArrayList<>();

        sanuzelList = new ArrayList<>();
        sanuzelNameList = new ArrayList<>();

        stekloList = new ArrayList<>();
        stekloNameList = new ArrayList<>();

        liftList = new ArrayList<>();
        liftNameList = new ArrayList<>();

        godaList = new ArrayList<>();
        godaNameList = new ArrayList<>();

        mebelList = new ArrayList<>();
        mebelNameList = new ArrayList<>();

        stenaList = new ArrayList<>();
        stenaNameList = new ArrayList<>();

        pereplanList = new ArrayList<>();
        pereplanNameList = new ArrayList<>();

        postroyList = new ArrayList<>();
        postroyNameList = new ArrayList<>();

        remontList = new ArrayList<>();
        remontNameList = new ArrayList<>();

        uslovieList = new ArrayList<>();
        uslovieNameList = new ArrayList<>();

        windList = new ArrayList<>();
        windNameList = new ArrayList<>();

        address = findViewById(R.id.address);
        area = findViewById(R.id.area);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        gallery = findViewById(R.id.gallery);
        submit = findViewById(R.id.submit);
        category = findViewById(R.id.category);
        client = findViewById(R.id.client);
        balkon = findViewById(R.id.balkon);
        planir = findViewById(R.id.planir);
        sanuzel = findViewById(R.id.sanuzel);
        pl_zhil = findViewById(R.id.pl_zhil);
        pl_kitch = findViewById(R.id.pl_kitch);
        steklo = findViewById(R.id.steklo);
        nach_etash = findViewById(R.id.nach_etash);
        kon_etash = findViewById(R.id.kon_etash);
        lift = findViewById(R.id.lift);
        ceiling = findViewById(R.id.ceiling);
        goda = findViewById(R.id.goda);
        mebel = findViewById(R.id.mebel);
        stena = findViewById(R.id.stena);
        pereplan = findViewById(R.id.pereplan);
        postroy = findViewById(R.id.postroy);
        remont = findViewById(R.id.remont);
        wind = findViewById(R.id.wind);
        exs = findViewById(R.id.exs);
        sogl = findViewById(R.id.sogl);
        info = findViewById(R.id.info);
        online = findViewById(R.id.online);
        yandex = findViewById(R.id.yandex);
        cian = findViewById(R.id.cian);
        domclick = findViewById(R.id.domclick);
        uslovie = findViewById(R.id.uslovie);
        obmen = findViewById(R.id.obmen);
        sdelka = findViewById(R.id.sdelka);
        video = findViewById(R.id.video);
        viewvideo = findViewById(R.id.viewvideo);
        lladdress = findViewById(R.id.lladdress);
        lldata_sozd = findViewById(R.id.lldata_sozd);
        data_sozd = findViewById(R.id.data_sozd);
        lldata_do = findViewById(R.id.lldata_do);
        data_do = findViewById(R.id.data_do);
        cadast = findViewById(R.id.cadast);
        razd_komnat = findViewById(R.id.razd_komnat);
        apart = findViewById(R.id.apart);
        osnovafoto = findViewById(R.id.osnovafoto);
        osnovafoto.setNestedScrollingEnabled(false);

        backbtn = findViewById(R.id.back_btn);
        video.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int r = editable.length();
                if(r>0) {
                    viewvideo.setVisibility(View.VISIBLE);
                } else {
                    viewvideo.setVisibility(View.GONE);
                }
            }
        });
        viewvideo.setOnClickListener(view -> {
            String vidos = video.getText().toString();
            String[] vidos1 = vidos.split("=");
            if(vidos1.length>0) {
                String DEVELOPER_KEY = "AIzaSyAOmKcpP0ZlIKRkeAaMoaiochQgFO8s8Mg";


                final Dialog dialog = new Dialog(AddPropertyActivity.this);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.video);
                dialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                youTubePlayerFragment = (YouTubePlayerSupportFragmentX) getSupportFragmentManager().findFragmentById(R.id.youtube_player_fragment);
                dialog.setOnDismissListener(dialogInterface -> {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.remove(youTubePlayerFragment);
                    fragmentTransaction.commit();
                });
                if (youTubePlayerFragment == null)
                    return;

                youTubePlayerFragment.initialize(DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                        boolean wasRestored) {
                        if (!wasRestored) {
                            youTubePlayer = player;
                            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                            youTubePlayer.loadVideo(vidos1[1]);
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                        //String h = arg1.toString();
                    }
                });

                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
    });
        ResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if(result.getData() != null) {
                            String addressset = result.getData().getStringExtra(PicklocationActivity.LOCATION_NAME);
                            LatLng latLng = result.getData().getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
                            address.setText(addressset);
                            destinationLocation = latLng;
                        }
                    }
                });

        lladdress.setOnClickListener(v -> finish());

        lladdress.setOnClickListener(v -> {
            Intent intent = new Intent(AddPropertyActivity.this, PicklocationActivity.class);
            intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
            ResultLauncher.launch(intent);
        });

        lldata_sozd.setOnClickListener(v -> finish());

        lldata_sozd.setOnClickListener(view -> {
            final View mDialog = getLayoutInflater().inflate(R.layout.calendar, null);
            calendar.CalendarView calendar = mDialog.findViewById(R.id.calendar);
            calendar.setOnDayClickListener(eventDay -> {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String datetime = dateformat.format(clickedDayCalendar.getTime());
                data_sozd.setText(datetime);
                mBottomSheetDialog.dismiss();
            });
            mBottomSheetDialog = new BottomSheetDialog(AddPropertyActivity.this);
            mBottomSheetDialog.setContentView(mDialog);
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
        });


        lldata_do.setOnClickListener(v -> finish());

        lldata_do.setOnClickListener(view -> {
            final View mDialog = getLayoutInflater().inflate(R.layout.calendar, null);
            calendar.CalendarView calendar = mDialog.findViewById(R.id.calendar);
            calendar.setOnDayClickListener(eventDay -> {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String datetime = dateformat.format(clickedDayCalendar.getTime());
                data_do.setText(datetime);
                mBottomSheetDialog.dismiss();
            });
            mBottomSheetDialog = new BottomSheetDialog(AddPropertyActivity.this);
            mBottomSheetDialog.setContentView(mDialog);
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
        });

        submit.setOnClickListener(v -> {
            String getaddress = address.getText().toString();
            String getarea = area.getText().toString();
            String getprice = price.getText().toString();
            String getcadast = cadast.getText().toString();
            String getpl_zhil = pl_zhil.getText().toString();
            String getpl_kitch = pl_kitch.getText().toString();
            String getnach_etash = nach_etash.getText().toString();
            String getkon_etash = kon_etash.getText().toString();
            String getceiling = ceiling.getText().toString();
            String getdescription = description.getText().toString();
            String catt = categoriList.get(category.getSelectedItemPosition()).getCategoryTabl();
            if(catt.equals("1")) {
                if (TextUtils.isEmpty(getaddress)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.noaddress), Toast.LENGTH_SHORT).show();
                }  else if (TextUtils.isEmpty(getprice)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.noprice), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getcadast)) {
                        Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.nocadast), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getarea)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.noarea), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getpl_zhil)) {
                        Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.nopl_zhil), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getpl_kitch)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.nopl_kitch), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getnach_etash)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.nonach_etash), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getkon_etash)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.nokon_etash), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getceiling)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.noceiling), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(getdescription)) {
                    Toast.makeText(AddPropertyActivity.this, getResources().getString(R.string.nodesc), Toast.LENGTH_SHORT).show();
                } else {
                    uploadData();
                }
            }
        });


        backbtn.setOnClickListener(v -> finish());


        gallery.setOnClickListener(v -> choosegallery());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        data_sozd.setText(currentDate);
        getCategory();
        getClient();
        getBalkon();
        getPlanir();
        getSanuzel();
        getSteklo();
        getStena();
        getPereplan();
        getPostroy();
        getRemont();
        getWind();

        LiftModels item1 = new LiftModels();
        item1.setLiftId("1");
        liftNameList.add(getResources().getString(R.string.est));
        liftList.add(item1);

        LiftModels item2 = new LiftModels();
        item2.setLiftId("2");
        liftNameList.add(getResources().getString(R.string.noest));
        liftList.add(item2);

        ArrayAdapter<String> liftSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, liftNameList);
        liftSpinner.setDropDownViewResource(R.layout.spinner);
        lift.setAdapter(liftSpinner);
        lift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        for(int y=1890;y<=2022;y++) {
            GodaModels item = new GodaModels();
            item.setGodaId(String.valueOf(y));
            godaNameList.add(String.valueOf(y));
            godaList.add(item);
        }

        ArrayAdapter<String> godaSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, godaNameList);
        godaSpinner.setDropDownViewResource(R.layout.spinner);
        goda.setAdapter(godaSpinner);
        goda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        MebelModels item5 = new MebelModels();
        item5.setMebelId("0");
        mebelNameList.add(getResources().getString(R.string.notukaz));
        mebelList.add(item5);


        MebelModels item3 = new MebelModels();
        item3.setMebelId("1");
        mebelNameList.add(getResources().getString(R.string.est));
        mebelList.add(item3);

        MebelModels item4 = new MebelModels();
        item4.setMebelId("2");
        mebelNameList.add(getResources().getString(R.string.noest));
        mebelList.add(item4);

        ArrayAdapter<String> mebelSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, mebelNameList);
        mebelSpinner.setDropDownViewResource(R.layout.spinner);
        mebel.setAdapter(mebelSpinner);
        mebel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        UslovieModels item6 = new UslovieModels();
        item6.setUslovieId("0");
        uslovieNameList.add(getResources().getString(R.string.notukaz));
        uslovieList.add(item6);


        UslovieModels item7 = new UslovieModels();
        item7.setUslovieId("1");
        uslovieNameList.add(getResources().getString(R.string.chp));
        uslovieList.add(item7);

        UslovieModels item8 = new UslovieModels();
        item8.setUslovieId("2");
        uslovieNameList.add(getResources().getString(R.string.obmen));
        uslovieList.add(item8);

        ArrayAdapter<String> uslovieSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, uslovieNameList);
        uslovieSpinner.setDropDownViewResource(R.layout.spinner);
        uslovie.setAdapter(uslovieSpinner);
        uslovie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mDataProvider = new ImageProvider(k);

        final DraggableAdapter myItemAdapter = new DraggableAdapter(getDataProvider(), this);
        mAdapter = myItemAdapter;
        mAdapter.setItemMoveMode(RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT);
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter);      // wrap for dragging

        final GeneralItemAnimator animator = new DraggableItemAnimator();

        osnovafoto.setLayoutManager(mLayoutManager);
        osnovafoto.setAdapter(mWrappedAdapter);
        osnovafoto.setItemAnimator(animator);

        osnovafoto.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.material_shadow_z1))));
        osnovafoto.addItemDecoration(new GridSpacingItemDecoration(1, 10, true));
        mRecyclerViewDragDropManager.attachRecyclerView(osnovafoto);
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

    public void getDataCategory(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        CategoryModels item = new CategoryModels();
                        item.setCategoryId(userdata.getString("cid"));
                        item.setCategoryTabl(userdata.getString("tabl"));
                        catNameList.add(userdata.getString("cname"));
                        categoriList.add(item);

                        ArrayAdapter<String> catSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, catNameList);
                        catSpinner.setDropDownViewResource(R.layout.spinner);
                        category.setAdapter(catSpinner);
                        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getClient() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.CLIENT, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataClient(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataClient(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        ClientModels item = new ClientModels();
                        item.setClientId(userdata.getString("id"));
                        clientNameList.add(userdata.getString("name"));
                        clientList.add(item);

                        ArrayAdapter<String> clientSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, clientNameList);
                        clientSpinner.setDropDownViewResource(R.layout.spinner);
                        client.setAdapter(clientSpinner);
                        client.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }





    private void getBalkon() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.BALKON, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataBalkon(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataBalkon(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        BalkonModels item = new BalkonModels();
                        item.setBalkonId(userdata.getString("id"));
                        balkonNameList.add(userdata.getString("name"));
                        balkonList.add(item);

                        ArrayAdapter<String> balkonSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, balkonNameList);
                        balkonSpinner.setDropDownViewResource(R.layout.spinner);
                        balkon.setAdapter(balkonSpinner);
                        balkon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getSanuzel() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SANUZEL, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataSanuzel(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataSanuzel(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        SanuzelModels item = new SanuzelModels();
                        item.setSanuzelId(userdata.getString("id"));
                        sanuzelNameList.add(userdata.getString("name"));
                        sanuzelList.add(item);

                        ArrayAdapter<String> sanuzelSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, sanuzelNameList);
                        sanuzelSpinner.setDropDownViewResource(R.layout.spinner);
                        sanuzel.setAdapter(sanuzelSpinner);
                        sanuzel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getPlanir() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.PLANIR, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataPlanir(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataPlanir(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        PlanirModels item = new PlanirModels();
                        item.setPlanirId(userdata.getString("id"));
                        planirNameList.add(userdata.getString("name"));
                        planirList.add(item);

                        ArrayAdapter<String> planirSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, planirNameList);
                        planirSpinner.setDropDownViewResource(R.layout.spinner);
                        planir.setAdapter(planirSpinner);
                        planir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getSteklo() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.STEKLO, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataSteklo(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataSteklo(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        StekloModels item = new StekloModels();
                        item.setStekloId(userdata.getString("id"));
                        stekloNameList.add(userdata.getString("name"));
                        stekloList.add(item);

                        ArrayAdapter<String> stekloSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, stekloNameList);
                        stekloSpinner.setDropDownViewResource(R.layout.spinner);
                        steklo.setAdapter(stekloSpinner);
                        steklo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getStena() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.STENA, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataStena(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataStena(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        StenaModels item = new StenaModels();
                        item.setStenaId(userdata.getString("id"));
                        stenaNameList.add(userdata.getString("name"));
                        stenaList.add(item);

                        ArrayAdapter<String> stenaSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, stenaNameList);
                        stenaSpinner.setDropDownViewResource(R.layout.spinner);
                        stena.setAdapter(stenaSpinner);
                        stena.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getPereplan() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.PEREPLAN, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataPereplan(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataPereplan(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        PereplanModels item = new PereplanModels();
                        item.setPereplanId(userdata.getString("id"));
                        pereplanNameList.add(userdata.getString("name"));
                        pereplanList.add(item);

                        ArrayAdapter<String> pereplanSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, pereplanNameList);
                        pereplanSpinner.setDropDownViewResource(R.layout.spinner);
                        pereplan.setAdapter(pereplanSpinner);
                        pereplan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getPostroy() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.POSTROY, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataPostroy(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataPostroy(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        PostroyModels item = new PostroyModels();
                        item.setPostroyId(userdata.getString("id"));
                        postroyNameList.add(userdata.getString("name"));
                        postroyList.add(item);

                        ArrayAdapter<String> postroySpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, postroyNameList);
                        postroySpinner.setDropDownViewResource(R.layout.spinner);
                        postroy.setAdapter(postroySpinner);
                        postroy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getRemont() {

        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.REMONT, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataRemont(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataRemont(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        RemontModels item = new RemontModels();
                        item.setRemontId(userdata.getString("id"));
                        remontNameList.add(userdata.getString("name"));
                        remontList.add(item);

                        ArrayAdapter<String> remontSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, remontNameList);
                        remontSpinner.setDropDownViewResource(R.layout.spinner);
                        remont.setAdapter(remontSpinner);
                        remont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }


    private void getWind() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.WIND, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataWind(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataWind(String loginData) {
        if (null != loginData && loginData.length() != 0) {
            try {
                JSONObject jsonObject = new JSONObject(loginData);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msg = jsonObject.getJSONArray("msg");
                    for (int i = 0; i < msg.length(); i++) {
                        JSONObject userdata = msg.getJSONObject(i);
                        WindModels item = new WindModels();
                        item.setWindId(userdata.getString("id"));
                        windNameList.add(userdata.getString("name"));
                        windList.add(item);

                        ArrayAdapter<String> windSpinner = new ArrayAdapter<>(AddPropertyActivity.this, R.layout.spinner, windNameList);
                        windSpinner.setDropDownViewResource(R.layout.spinner);
                        wind.setAdapter(windSpinner);
                        wind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {
                                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                                ((TextView) parent.getChildAt(0)).setTextSize(14);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    public void delitem(int id) {
        k.remove(id);
        mAdapter.notifyDataSetChanged();
    }

    public void choosegallery() {
        ImagePicker.with(this)
                .setFolderMode(true)
                .setFolderTitle(getResources().getString(R.string.addpic))
                .setImageTitle(getResources().getString(R.string.tap))
                .setMultipleMode(true)
                .setShowCamera(false)
                .setDirectoryName("Camera")
                .setRequestCode(203)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PicklocationActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String addressset = data.getStringExtra(PicklocationActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
                address.setText(addressset);
                destinationLocation = latLng;
            }
        }
         else if (requestCode == 203) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<Image> galleryset = ImagePicker.getImages(data);
                for(int i = 0; i< galleryset.size(); i++) {
                    Uri uri = Uri.fromFile(new File(galleryset.get(i).getPath()));
                    k.add(new DataImage(k.size(), 0, uri));
                    mAdapter.notifyDataSetChanged();
                    isgallery = true;
                }

            }
        }

    }

    public void uploadData() {
        String dog = "";
        if(exs.isChecked()) {
            dog = "exs";
        }
        if(sogl.isChecked()) {
            dog = "sogl";
        }
        if(info.isChecked()) {
            dog = "info";
        }
        String onl = "0";
        if(online.isChecked()) {
            onl = "1";
        }
        String yand = "0";
        if(yandex.isChecked()) {
            yand = "1";
        }
        String cia = "0";
        if(cian.isChecked()) {
            cia = "1";
        }
        String domcl = "0";
        if(domclick.isChecked()) {
            domcl = "1";
        }
        String data_sozd1 = data_sozd.getText().toString();
        final String[] split = data_sozd1.split("\\.");
        data_sozd1 = split[2] + "-" + split[1] + "-" + split[0];

        String data_do1 = data_do.getText().toString();
        final String[] split1 = data_do1.split("\\.");
        data_do1 = split1[2] + "-" + split1[1] + "-" + split1[0];

        RequestHttp request = RequestHttp.create(Constants.ADDPROPERTY);
        request.setMethod("POST")
                .setTimeout(0)
                .setLogger(new Logger(Logger.ERROR))
                .addParameter("agent", MainActivity.user_id)
                .addParameter("tel_agent", MainActivity.tel_agent)
                .addParameter("type", categoriList.get(category.getSelectedItemPosition()).getCategoryId())
                .addParameter("tabl", categoriList.get(category.getSelectedItemPosition()).getCategoryTabl())
                .addParameter("comments", new String(description.getText().toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .addParameter("price", price.getText().toString())
                .addParameter("fulladdress",  new String(address.getText().toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .addParameter("data_sozd", data_sozd1)
                .addParameter("data_do", data_do1)
                .addParameter("exs", dog)
                .addParameter("online", onl)
                .addParameter("yandexo", yand)
                .addParameter("cian", cia)
                .addParameter("domclick", domcl)
                .addParameter("client", clientList.get(client.getSelectedItemPosition()).getClientId())
                .addParameter("pl_ob", area.getText().toString())
                .addParameter("apart", apart.getText().toString())
                .addParameter("cadast", cadast.getText().toString())
                .addParameter("razd_komnat", razd_komnat.getText().toString())
                .addParameter("balkon", balkonList.get(balkon.getSelectedItemPosition()).getBalkonId())
                .addParameter("planir", planirList.get(planir.getSelectedItemPosition()).getPlanirId())
                .addParameter("sanuzel", sanuzelList.get(sanuzel.getSelectedItemPosition()).getSanuzelId())
                .addParameter("pl_zhil", pl_zhil.getText().toString())
                .addParameter("pl_kitch", pl_kitch.getText().toString())
                .addParameter("steklo", stekloList.get(steklo.getSelectedItemPosition()).getStekloId())
                .addParameter("nach_etash", nach_etash.getText().toString())
                .addParameter("kon_etash", kon_etash.getText().toString())
                .addParameter("lift", liftList.get(lift.getSelectedItemPosition()).getLiftId())
                .addParameter("ceiling", ceiling.getText().toString())
                .addParameter("goda", godaList.get(goda.getSelectedItemPosition()).getGodaId())
                .addParameter("mebel", mebelList.get(mebel.getSelectedItemPosition()).getMebelId())
                .addParameter("stena", stenaList.get(stena.getSelectedItemPosition()).getStenaId())
                .addParameter("pereplan", pereplanList.get(pereplan.getSelectedItemPosition()).getPereplanId())
                .addParameter("postroy", postroyList.get(postroy.getSelectedItemPosition()).getPostroyId())
                .addParameter("remont", remontList.get(remont.getSelectedItemPosition()).getRemontId())
                .addParameter("wind", windList.get(wind.getSelectedItemPosition()).getWindId())
                .addParameter("uslovie", uslovieList.get(uslovie.getSelectedItemPosition()).getUslovieId())
                .addParameter("obmen",  new String(obmen.getText().toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .addParameter("sdelka", new String(sdelka.getText().toString().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                .addParameter("video", video.getText().toString())
                .addParameter("latitude", destinationLocation.latitude)
                .addParameter("longitude", destinationLocation.longitude);

        if (isgallery) {
            ArrayList<Image> al = new ArrayList<>();
            for(int u=0;u<k.size();u++) {
                Image d = new Image(u,"name", k.get(u).getUri(),k.get(u).getUri().getPath(),0,"");
                al.add(d);
            }
                request.addParameter("galleryimage[]", al);
        }

        request.setFileUploadListener(new FileUploadListener() {
            @Override
            public void onUploadingFile(File file, long size, long uploaded) {

            }
        })
                .setRequestStateListener(new RequestStateListener() {
                    @Override
                    public void onStart() {
                        dialog.setMessage(getResources().getString(R.string.wait));
                        dialog.setIndeterminate(false);
                        dialog.setCancelable(true);
                        dialog.show();
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onConnectionError(Exception e) {
                        e.printStackTrace();
                    }
                })
                .setResponseListener(new JsonResponseListener() {
                    @Override
                    public void onOkResponse(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray("200");
                        JSONObject objJson = jsonArray.getJSONObject(0);
                        Constants.successmsg = objJson.getInt("success");
                        message = objJson.getString("msg");
                        if (Constants.successmsg == 0) {

                            Toast.makeText(AddPropertyActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPropertyActivity.this, message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPropertyActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(JSONObject jsonObject) {
                        String er = "";
                    }

                    @Override
                    public void onParseError(JSONException e) {
                        String df = e.toString();
                    }
                }).execute();
    }

    public AbstractDataProvider getDataProvider() {
        return mDataProvider;
    }

    @Override
    public void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager.release();
            mRecyclerViewDragDropManager = null;
        }

        if (osnovafoto != null) {
            osnovafoto.setItemAnimator(null);
            osnovafoto.setAdapter(null);
            osnovafoto = null;
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter);
            mWrappedAdapter = null;
        }
        mAdapter = null;
        mLayoutManager = null;

        super.onDestroy();
    }

}

