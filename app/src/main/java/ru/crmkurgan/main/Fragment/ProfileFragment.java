package ru.crmkurgan.main.Fragment;


import static android.content.Context.MODE_PRIVATE;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import ru.crmkurgan.main.Activity.AboutActivity;
import ru.crmkurgan.main.Activity.LoginFormActivity;
import ru.crmkurgan.main.Activity.MyPropertyActivity;
import ru.crmkurgan.main.Activity.PrivacyActivity;
import ru.crmkurgan.main.BuildConfig;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Activity.MainActivity;
import ru.crmkurgan.main.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static ru.crmkurgan.main.Activity.MainActivity.user_id;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ProfileFragment extends Fragment {

    View setView;
    Context context;
    ru.crmkurgan.main.Constants.BaseApp BaseApp;

    Button logout, login, edit;
    CircleImageView imageProfile;
    DatabaseReference rootref;

    TextView fullName;

    LinearLayout llshare, llrate, llabout, llprivacy, llproperty;

    public static SharedPreferences sharedPreferences;
    String agent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setView = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        BaseApp = ru.crmkurgan.main.Constants.BaseApp.getInstance();
        llabout = setView.findViewById(R.id.llabout);
        llrate = setView.findViewById(R.id.llrate);
        llshare = setView.findViewById(R.id.llshare);
        llproperty = setView.findViewById(R.id.llproperty);
        login = setView.findViewById(R.id.login);
        logout = setView.findViewById(R.id.logout);
        edit = setView.findViewById(R.id.editprofile);
        imageProfile = setView.findViewById(R.id.profileimage);

        sharedPreferences = requireActivity().getSharedPreferences(Constants.pref_name, MODE_PRIVATE);
        agent = sharedPreferences.getString("agent","0");
        if(agent.equals("0")) {
            llproperty.setVisibility(View.GONE);
        }
        llproperty.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyPropertyActivity.class);
            startActivity(intent);

        });
        llshare.setOnClickListener(v -> {
    try {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        String shareMessage= "\n" + getResources().getString(R.string.shareapp) + "\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    } catch(Exception e) {
        //e.toString();
    }
        });

        llrate.setOnClickListener(v -> {
            Uri uri = Uri.parse("market://details?id=" + requireActivity().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + requireActivity().getPackageName())));
            }
        });

        edit.setOnClickListener(v -> Editprofile());

        login.setOnClickListener(v -> {
            Intent intent= new Intent(getActivity(), LoginFormActivity.class);
            startActivity(intent);

        });

        logout.setOnClickListener(v -> clickDone());
        rootref= FirebaseDatabase.getInstance().getReference();

        fullName = setView.findViewById(R.id.fullname);
        llprivacy = setView.findViewById(R.id.llpirvacy);

        llabout.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });
        llprivacy.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PrivacyActivity.class);
            startActivity(intent);
        });


        return setView;
    }
    @Override
    public void onStart() {
        super.onStart();
        GetUser();
    }




    public void doreload() {
        GetUser();
       // getParentFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
    }

    private void GetUser() {
        if (BaseApp.getIsLogin())
        {
            login.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            JSONObject parameters = new JSONObject();
            RequestQueue rq = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, Constants.USERDATA+"&userid="+user_id, parameters, response -> {
                        String respo = response.toString();
                        Log.d("responce", respo);
                        userinfo(respo);
                    }, error -> Log.d("respo", error.toString()));
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rq.getCache().clear();
            rq.add(jsonObjectRequest);
        } else {
            login.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            llproperty.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }

    }


    public void userinfo(String loginData) {
                try {
                    JSONObject jsonObject = new JSONObject(loginData);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONArray msg = jsonObject.getJSONArray("msg");
                        JSONObject userdata = msg.getJSONObject(0);
                        if(userdata.optString("agent").equals("1")) {
                            edit.setVisibility(View.GONE);
                        }
                        fullName.setText(userdata.optString("fullname"));
                        if(!userdata.optString("imageprofile").equals("")) {
                            String frf = userdata.optString("imageprofile");
                            Picasso.get()
                                    .load(frf)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .fit()
                                    .centerCrop()
                                    .into(imageProfile);
                        }

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }



    public void Editprofile(){
        EditProfileFragment editProfile_fragment = new EditProfileFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, editProfile_fragment).commit();
    }

    public  void Logout(){
        SharedPreferences.Editor editor= MainActivity.sharedPreferences.edit();
        editor.putString(Constants.uid,"").clear();
        editor.apply();
        rootref.child("Users").child(user_id).child("token").setValue("null");
        BaseApp.saveIsLogin(false);
        Intent intent = new Intent(requireActivity().getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getParentFragmentManager().beginTransaction().detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
    }
    public void clickDone() {
        new AlertDialog.Builder(requireActivity(), R.style.AlertDialog)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.logoutqe))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    Logout();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

}
