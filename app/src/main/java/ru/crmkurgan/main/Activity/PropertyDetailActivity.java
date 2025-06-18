package ru.crmkurgan.main.Activity;

import static ru.crmkurgan.main.Utils.Tools.setSystemBarColor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import ratingview.RatingView;
import ru.crmkurgan.main.Constants.BaseApp;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Fragment.ChatFragment;
import ru.crmkurgan.main.Item.GalleryItem;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.DatabaseHelper;
import ru.crmkurgan.main.Utils.NetworkUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PropertyDetailActivity extends AppCompatActivity {
    TextView propName, address, nameuser, price, ratenow;
    String Id, Tabl;
    ImageView imageuser, images, backButton, likeButton, chat, phone, shareButton;
    PropertyModels item;
    RelativeLayout progress;
    LinearLayout llprofile;
    RatingView ratingView;
    DatabaseHelper databaseHelper;
    ArrayList<PropertyModels> mPropertyList;
    ArrayList<String> addgallery;
    WebView description;
    RecyclerView gallery;
    FloatingActionButton fab;
    GalleryItem galleryItem;
    BaseApp baseApp;
    CardView rledit;
    ProgressDialog pDialog;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    LinearLayout vtor,doma,commercy;
    TextView komnatnost,pl_ob,planir,remont,sanuzel,balkon,etash,postroy,stena;
    TextView pl_obdom,razd_komnat,etashnost,postroydom,stenadom,electro,otop,gaz,canalya,zemlya;
    TextView pl_obcommercy,etashcommercy;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fab.setVisibility(View.VISIBLE);
        setSystemBarColor(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propertydetail);
        setSystemBarColor(this);
        //setRootView(this);
        Intent i = getIntent();
        Id = i.getStringExtra("Id");
        Tabl = i.getStringExtra("Tabl");
        vtor = findViewById(R.id.vtor);
        doma = findViewById(R.id.doma);
        commercy = findViewById(R.id.commercy);
        if(Tabl.equals("1")) {
            vtor.setVisibility(View.VISIBLE);
        }
        if(Tabl.equals("2")) {
            doma.setVisibility(View.VISIBLE);
        }
        if(Tabl.equals("3")) {
            commercy.setVisibility(View.VISIBLE);
        }

        View rating_sheet = findViewById(R.id.rating_sheet);
        mBehavior = BottomSheetBehavior.from(rating_sheet);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        item = new PropertyModels();
        baseApp = BaseApp.getInstance();
        mPropertyList = new ArrayList<>();
        addgallery = new ArrayList<>();
        phone = findViewById(R.id.phone);
        propName = findViewById(R.id.propertyname);
        fab = findViewById(R.id.fab);
        progress = findViewById(R.id.progress);
        address = findViewById(R.id.address);
        nameuser = findViewById(R.id.name);
        imageuser = findViewById(R.id.imageuser);
        images = findViewById(R.id.image);
        ratingView = findViewById(R.id.ratingView);
        backButton = findViewById(R.id.back_btn);
        likeButton = findViewById(R.id.like_btn);
        shareButton = findViewById(R.id.share_btn);
        price = findViewById(R.id.propertyprice);
        ratenow =findViewById(R.id.rate);
        description = findViewById(R.id.description);
        gallery = findViewById(R.id.galleryre);
        chat = findViewById(R.id.chat);
        llprofile = findViewById(R.id.llprofile);
        rledit = findViewById(R.id.rledit);
        rledit.setOnClickListener(view -> {
            Intent intent = new Intent(PropertyDetailActivity.this, EditPropertyActivity.class);
            intent.putExtra("Id",Id);
            intent.putExtra("Tabl", Tabl);
            startActivity(intent);
        });
        komnatnost = findViewById(R.id.komnatnost);
        pl_ob = findViewById(R.id.pl_ob);
        planir = findViewById(R.id.planir);
        remont = findViewById(R.id.remont);
        sanuzel = findViewById(R.id.sanuzel);
        balkon = findViewById(R.id.balkon);
        etash = findViewById(R.id.etash);
        postroy = findViewById(R.id.postroy);
        stena = findViewById(R.id.stena);

        pl_obdom = findViewById(R.id.pl_obdom);
        razd_komnat = findViewById(R.id.razd_komnat);
        etashnost = findViewById(R.id.etashnost);
        postroydom = findViewById(R.id.postroydom);
        stenadom = findViewById(R.id.stenadom);
        electro = findViewById(R.id.electro);
        otop = findViewById(R.id.otop);
        gaz = findViewById(R.id.gaz);
        canalya = findViewById(R.id.canalya);
        zemlya = findViewById(R.id.zemlya);
        pl_obcommercy = findViewById(R.id.pl_obcommercy);
        etashcommercy = findViewById(R.id.etashcommercy);
        fab.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        ratenow.setOnClickListener(v -> rateNow());

        chat.setOnClickListener(v -> {
            if (baseApp.getIsLogin()) {
                fab.setVisibility(View.GONE);
                setSystemBarColor(this);
                chatFragment(MainActivity.user_id, item.getUserId(), item.getNameUser(), item.getImageUser());
            } else {
                Intent intent = new Intent(PropertyDetailActivity.this, LoginFormActivity.class);
                startActivity(intent);
            }
        });

        gallery.setHasFixedSize(true);
        gallery.setNestedScrollingEnabled(false);
        gallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backButton.setOnClickListener(v -> finish());

        likeButton.setOnClickListener(v -> {
            ContentValues fav = new ContentValues();
            if (databaseHelper.getFavouriteById(item.getPropid(),item.getTabl())) {
                databaseHelper.removeFavouriteById(item.getPropid(),item.getTabl());
                likeButton.setColorFilter(getResources().getColor(R.color.gray));
                Toast.makeText(PropertyDetailActivity.this, getResources().getString(R.string.remove), Toast.LENGTH_SHORT).show();
            } else {
                fav.put(DatabaseHelper.KEY_ID, item.getPropid());
                fav.put(DatabaseHelper.KEY_TITLE, item.getName());
                fav.put(DatabaseHelper.KEY_IMAGE, item.getImage());
                fav.put(DatabaseHelper.KEY_RATE, item.getRateAvg());
                fav.put(DatabaseHelper.KEY_ADDRESS, item.getAddress());
                fav.put(DatabaseHelper.KEY_PRICE, item.getPrice());
                fav.put(DatabaseHelper.KEY_TABL, item.getTabl());
                if(Tabl.equals("1")) {
                    fav.put(DatabaseHelper.KEY_KOMNATNOST, item.getKomnatnost());
                    fav.put(DatabaseHelper.KEY_PL_OB, item.getPl_ob());
                    fav.put(DatabaseHelper.KEY_PLANIR, item.getPlanir());
                    fav.put(DatabaseHelper.KEY_REMONT, item.getRemont());
                    fav.put(DatabaseHelper.KEY_SANUZEL, item.getSanuzel());
                    fav.put(DatabaseHelper.KEY_BALKON, item.getBalkon());
                    fav.put(DatabaseHelper.KEY_ETASH, item.getEtash());
                    fav.put(DatabaseHelper.KEY_POSTROY, item.getPostroy());
                    fav.put(DatabaseHelper.KEY_STENA, item.getStena());
                }
                if(Tabl.equals("2")) {
                    fav.put(DatabaseHelper.KEY_PL_OB, item.getPl_ob());
                    fav.put(DatabaseHelper.KEY_ETASH, item.getEtash());
                    fav.put(DatabaseHelper.KEY_POSTROY, item.getPostroy());
                    fav.put(DatabaseHelper.KEY_STENA, item.getStena());
                    fav.put(DatabaseHelper.KEY_RAZD_KOMNAT, item.getRazd_komnat());
                    fav.put(DatabaseHelper.KEY_ELECTRO, item.getElectro());
                    fav.put(DatabaseHelper.KEY_OTOP, item.getOtop());
                    fav.put(DatabaseHelper.KEY_GAZ, item.getGaz());
                    fav.put(DatabaseHelper.KEY_CANALYA, item.getCanalya());
                    fav.put(DatabaseHelper.KEY_ZEMLYA, item.getZemlya());
                }
                if(Tabl.equals("3")) {
                    fav.put(DatabaseHelper.KEY_PL_OB, item.getPl_ob());
                    fav.put(DatabaseHelper.KEY_ETASH, item.getEtash());
                }
                fav.put(DatabaseHelper.KEY_TABL, item.getTabl());
                databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                likeButton.setColorFilter(getResources().getColor(R.color.red));
                Toast.makeText(PropertyDetailActivity.this, getResources().getString(R.string.addfav), Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String shareMessage= item.getUrl();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        });


        phone.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + item.getPhone()));
            startActivity(callIntent);
        });
        isFavourite();
        getData();
    }

    @SuppressLint("MissingPermission")
    private void rateNow() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View mDialog = getLayoutInflater().inflate(R.layout.sheet_rating, null);
        ImageView btnclose = mDialog.findViewById(R.id.bt_close);
        final RatingView ratingView = mDialog.findViewById(R.id.ratingView);
        Button submit = mDialog.findViewById(R.id.submit);
        final String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        ratingView.setRating(0);

        btnclose.setOnClickListener(view -> mBottomSheetDialog.hide());

        submit.setOnClickListener(view -> {
            pDialog = new ProgressDialog(PropertyDetailActivity.this, R.style.AlertDialog);
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
            if (NetworkUtils.isConnected(PropertyDetailActivity.this)) {
                JSONObject parameters = new JSONObject();
                RequestQueue rq = Volley.newRequestQueue(PropertyDetailActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, Constants.RATING+Id+"&rate="+ ratingView.getRating()+"&device_id="+ deviceId + "&tabl3=" + item.getTabl(), parameters, response -> {
                            String respo=response.toString();
                            Log.d("responce",respo);
                            pDialog.dismiss();
                            try {
                                JSONObject jsonObject=new JSONObject(respo);
                                String msg=jsonObject.optString("msg");
                                Toast.makeText(PropertyDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }, error -> {
                            Log.d("respo",error.toString());
                            Toast.makeText(PropertyDetailActivity.this, getResources().getString(R.string.problem), Toast.LENGTH_SHORT).show();
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rq.getCache().clear();
                rq.add(jsonObjectRequest);
                mBottomSheetDialog.hide();
            } else {
                Toast.makeText(PropertyDetailActivity.this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            }

        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(mDialog);
        mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(dialog -> mBottomSheetDialog = null);
    }

    private void getData() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.PROPPERTYDETAIL+Id + "&tabl2=" + Tabl, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getDataProperty(respo);
                    getDataImage(respo);
                    progress.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getDataProperty(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    item.setPropid(userdata.getString("propid"));
                    item.setName(userdata.getString("name"));
                    item.setAddress(userdata.getString("address"));
                    item.setUserId(userdata.getString("userid"));
                    item.setNameUser(userdata.getString("fullname"));
                    item.setImageUser(userdata.getString("imageprofile"));
                    item.setCid(userdata.getString("cid"));
                    item.setCname(userdata.getString("cname"));
                    item.setRateAvg(userdata.getString("rate"));
                    item.setImage(userdata.getString("image"));
                    item.setPrice(userdata.getString("price"));
                    item.setLatitude(userdata.getString("latitude"));
                    item.setLongitude(userdata.getString("longitude"));
                    item.setDescription(userdata.getString("description"));
                    item.setTabl(userdata.getString("tabl"));
                    item.setPhone(userdata.getString("tel_agent"));
                    item.setUrl(userdata.getString("url"));
                    if(Tabl.equals("1")) {
                        item.setKomnatnost(userdata.getString("komnatnost"));
                        item.setPl_ob(userdata.getString("pl_ob"));
                        item.setPlanir(userdata.getString("planir"));
                        item.setRemont(userdata.getString("remont"));
                        item.setSanuzel(userdata.getString("sanuzel"));
                        item.setBalkon(userdata.getString("balkon"));
                        item.setEtash(userdata.getString("etash"));
                        item.setPostroy(userdata.getString("postroy"));
                        item.setStena(userdata.getString("stena"));
                    }
                    if(Tabl.equals("2")) {
                        item.setPl_ob(userdata.getString("pl_ob"));
                        item.setEtash(userdata.getString("etash"));
                        item.setPostroy(userdata.getString("postroy"));
                        item.setStena(userdata.getString("stena"));
                        item.setRazd_komnat(userdata.getString("razd_komnat"));
                        item.setElectro(userdata.getString("electro"));
                        item.setOtop(userdata.getString("otop"));
                        item.setGaz(userdata.getString("gaz"));
                        item.setCanalya(userdata.getString("canalya"));
                        item.setZemlya(userdata.getString("zemlya"));
                    }
                    if(Tabl.equals("3")) {
                        item.setPl_ob(userdata.getString("pl_ob"));
                        item.setEtash(userdata.getString("etash"));
                    }

                    if(item.getUserId().equals(MainActivity.user_id)) {
                        llprofile.setVisibility(View.GONE);
                        rledit.setVisibility(View.VISIBLE);
                    } else {
                        llprofile.setVisibility(View.VISIBLE);
                        rledit.setVisibility(View.GONE);
                    }

                        Spanned html = Html.fromHtml(item.getName());
                        propName.setText(html);
                        address.setText(Html.fromHtml(item.getAddress()));
                        if(Tabl.equals("1")) {
                            komnatnost.setText(item.getKomnatnost());
                            pl_ob.setText(Html.fromHtml(item.getPl_ob()));
                            planir.setText(item.getPlanir());
                            remont.setText(item.getRemont());
                            sanuzel.setText(item.getSanuzel());
                            balkon.setText(item.getBalkon());
                            etash.setText(item.getEtash());
                            postroy.setText(item.getPostroy());
                            stena.setText(item.getStena());
                        }
                        if(Tabl.equals("2")) {
                            pl_obdom.setText(Html.fromHtml(item.getPl_ob()));
                            razd_komnat.setText(item.getRazd_komnat());
                            etashnost.setText(item.getEtash());
                            postroydom.setText(item.getPostroy());
                            stenadom.setText(item.getStena());
                            electro.setText(item.getElectro());
                            otop.setText(item.getOtop());
                            gaz.setText(item.getGaz());
                            canalya.setText(item.getCanalya());
                            zemlya.setText(item.getZemlya());
                        }
                        if(Tabl.equals("3")) {
                            pl_obcommercy.setText(Html.fromHtml(item.getPl_ob()));
                            etashcommercy.setText(item.getEtash());
                        }
                        ratingView.setRating(Float.parseFloat(item.getRateAvg()));
                        nameuser.setText(item.getNameUser());
                        price.setText(item.getPrice());
                        Picasso.get()
                                .load(item.getImageUser())
                                .fit().centerCrop()
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(imageuser);
                        Picasso.get()
                                .load(item.getImage()).fit().centerCrop()
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                .placeholder(R.drawable.image_placeholder)
                                .into(images);

                        String mimeType = "text/html";
                        String encoding = "utf-8";
                        String htmlText = item.getDescription();

                        String text = "<html dir=" + "><head>"
                                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/NeoSans_Pro_Regular.ttf\")}body{font-family: MyFont;color: #a5a5a5;text-align:left;line-height:1.2}"
                                + "</style></head>"
                                + "<body>"
                                + htmlText
                                + "</body></html>";

                        description.loadDataWithBaseURL(null, text, mimeType, encoding, null);
                        fab.setOnClickListener(v -> {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + item.getLatitude() + "," + item.getLongitude() + " (" + Html.fromHtml(item.getName()) + ")";
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            startActivity(intent);
                        });


                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public void getDataImage(String result){
        try {
            JSONObject jsonObject=new JSONObject(result);
            String code=jsonObject.optString("code");
            if(code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject userdata = msg.getJSONObject(i);
                    addgallery.add(userdata.getString("image"));
                    JSONArray username_obj = userdata.getJSONArray("galleryimage");
                    for (int j = 0; j < username_obj.length(); j++) {
                        JSONObject userdata1 = username_obj.getJSONObject(j);
                        addgallery.add(userdata1.optString("gallery"));
                    }
                }

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
        galleryItem = new GalleryItem(this, addgallery, R.layout.item_square);
        gallery.setAdapter(galleryItem);
        galleryItem.setOnItemClickListener((view, viewModel, pos) -> {
            Intent i = new Intent(PropertyDetailActivity.this, FullImageActivity.class);
            i.putExtra(FullImageActivity.EXTRA_POS, pos);
            i.putStringArrayListExtra(FullImageActivity.EXTRA_IMGS, addgallery);
            startActivity(i);
        });

    }



    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id,Tabl)) {
            likeButton.setColorFilter(getResources().getColor(R.color.red));
        } else {
            likeButton.setColorFilter(getResources().getColor(R.color.gray));
        }
    }
    public void chatFragment(String senderid,String receiverid,String name,String picture){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();

    }
}
