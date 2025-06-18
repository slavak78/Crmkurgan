package ru.crmkurgan.main.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dadata.interfaces.OnSuggestionsListener;
import dadata.realm.models.dadata.RealmDaDataAnswer;
import dadata.realm.models.dadata.RealmDaDataSuggestion;
import dadata.rest.DaDataBody;
import dadata.rest.DaDataRestClient;
import dadata.ui.DaDataArrayAdapter;
import dadata.utils.ServerUtils;
import io.realm.RealmList;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PicklocationActivity extends AppCompatActivity
        implements OnMapReadyCallback, TextWatcher, OnSuggestionsListener {

    private static final int REQUEST_PERMISSION_LOCATION = 991;

    public static final int LOCATION_PICKER_ID = 78;
    public static final String FORM_VIEW_INDICATOR = "FormToFill";

    public static final String LOCATION_NAME = "LocationName";
    public static final String LOCATION_LATLNG = "LocationLatLng";


    AutoCompleteTextView autoCompleteTextView;
    private Toast toast;
    TextView currentAddress;

    Button selectLocation;
    ImageView backbutton;


    private GoogleMap gMap;

    private int formToFill;
    private static final List<String> EMPTY = new ArrayList<>();
    private DaDataArrayAdapter<String> adapter;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picklocation);

        autoCompleteTextView = findViewById(R.id.locationPicker_autoCompleteText);
        currentAddress = findViewById(R.id.locationPicker_currentAddress);
        adapter = new DaDataArrayAdapter<>(this, R.layout.list_dadata, EMPTY);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> new Thread(() -> {
            try {
                RealmDaDataSuggestion suggestion = DaDataRestClient.getInstance().suggestSync(new DaDataBody(adapterView.getItemAtPosition(i).toString(), 1));
                RealmList<RealmDaDataAnswer> realmDaDataAnswers;
                realmDaDataAnswers = suggestion.getSuggestions();
                for (int y = 0; y < realmDaDataAnswers.size(); y++) {
                    RealmDaDataAnswer realmDaDataAnswer = realmDaDataAnswers.get(y);
                    if (realmDaDataAnswer != null) {
                        String geo_lat = realmDaDataAnswer.getRealmData().getGeo_lat();
                        String geo_lon = realmDaDataAnswer.getRealmData().getGeo_lon();
                        LatLng latLng = new LatLng(Double.parseDouble(geo_lat), Double.parseDouble(geo_lon));
                        PicklocationActivity.this.runOnUiThread(() -> gMap.moveCamera(CameraUpdateFactory.newLatLng(Objects.requireNonNull(latLng))));
                    }
                }
            } catch (Exception ignored) {

            }
        }).start());
        selectLocation = findViewById(R.id.locationPicker_destinationButton);
        backbutton = findViewById(R.id.back_btn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locationPicker_maps);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        formToFill = intent.getIntExtra(FORM_VIEW_INDICATOR, -1);

        selectLocation.setOnClickListener(view -> selectLocation());

        backbutton.setOnClickListener(view -> finish());

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(final Editable s) {
        ServerUtils.query(s.toString(), this);
    }

    @Override
    public synchronized void onSuggestionsReady(List<String> suggestions) {
        adapter.clear();

        adapter.addAll(suggestions);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(String message) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void selectLocation() {
        LatLng selectedLocation = gMap.getCameraPosition().target;
        String selectedAddress = currentAddress.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(FORM_VIEW_INDICATOR, formToFill);
        intent.putExtra(LOCATION_NAME, selectedAddress);
        intent.putExtra(LOCATION_LATLNG, selectedLocation);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    @SuppressLint("MissingPermission")
    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        gMap.setMyLocationEnabled(true);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15f)
            );
            gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        updateLastLocation();
        setupMapOnCameraChange();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            }
        }
    }

    private void setupMapOnCameraChange() {
        gMap.setOnCameraIdleListener(() -> {
            LatLng center = gMap.getCameraPosition().target;
            fillAddress(center);
        });
    }

    private void fillAddress(final LatLng latLng) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("lat", latLng.latitude);
            parameters.put("lon", latLng.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.BYLATLNG, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getAddress(respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }

    public void getAddress(String respo) {
        try {
            JSONObject jsonObject=new JSONObject(respo);
            JSONArray jsonArray = jsonObject.getJSONArray("suggestions");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userdata = jsonArray.getJSONObject(i);
                currentAddress.setText(userdata.getString("value"));
            }
        } catch (JSONException e) {

        e.printStackTrace();
    }
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoCompleteTextView.addTextChangedListener(this);
    }
}
