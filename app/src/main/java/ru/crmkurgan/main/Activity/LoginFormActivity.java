package ru.crmkurgan.main.Activity;

import static ru.crmkurgan.main.Utils.Tools.setSystemBarColor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.R;
import iosdialog.IOSDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.crmkurgan.main.Constants.BaseApp;
import java.util.concurrent.TimeUnit;

public class LoginFormActivity extends AppCompatActivity {
    BaseApp BaseApp;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth fbAuth;
    EditText phoneText, numOne, numTwo, numThree, numFour, numFive, numSix;
    TextView countryCode, sendTo;
    Button buttonLogin, confirmButton;
    ImageView backButton, backButtonverify;
    ViewFlipper viewFlipper;
    String phoneNumber;
    public IOSDialog iosDialog;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    TextView resend;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        setSystemBarColor(this);
        fbAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        BaseApp = ru.crmkurgan.main.Constants.BaseApp.getInstance();
        if (firebaseUser != null) {
            firebaseUser.getIdToken(true);
        }

        sharedPreferences=getSharedPreferences(Constants.pref_name,MODE_PRIVATE);

        phoneText= findViewById(R.id.phonenumber);

        countryCode = findViewById(R.id.countrycode);

        buttonLogin = findViewById(R.id.buttonlogin);
        buttonLogin.setOnClickListener(v -> Nextbtn(viewFlipper));

        backButton = findViewById(R.id.back_btn);
        backButton.setOnClickListener(v -> finish());

        backButtonverify = findViewById(R.id.back_btn_verify);
        backButtonverify.setOnClickListener(v -> finish());

        confirmButton = findViewById(R.id.buttonconfirm);
        confirmButton.setOnClickListener(v -> verifyCode(viewFlipper));

        sendTo = findViewById(R.id.sendtotxt);

        viewFlipper = findViewById(R.id.viewflipper);
        resend = findViewById(R.id.resend);
        resend.setOnClickListener(this::resendCode);
        codenumber();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

    }

    public void codenumber(){
        numOne = findViewById(R.id.numone);
        numTwo = findViewById(R.id.numtwo);
        numThree = findViewById(R.id.numthree);
        numFour = findViewById(R.id.numfour);
        numFive = findViewById(R.id.numfive);
        numSix = findViewById(R.id.numsix);

        numOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(numOne.getText().toString().length()==0){
                    numTwo.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(numTwo.getText().toString().length()==0){
                    numThree.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(numThree.getText().toString().length()==0){
                    numFour.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(numFour.getText().toString().length()==0){
                    numFive.requestFocus();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        numFive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(numFive.getText().toString().length()==0){
                    numSix.requestFocus();
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void Nextbtn(View view) {
        phoneNumber= countryCode.getText().toString()+phoneText.getText().toString();
        String ccode= countryCode.getText().toString();

        if((!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(ccode)) && phoneNumber.length()>11){
            iosDialog.show();
            Send_Number_tofirebase(phoneNumber);

        }else {
            Toast.makeText(this, getResources().getString(R.string.ernumber), Toast.LENGTH_SHORT).show();
        }
    }





    public void Send_Number_tofirebase(String phoneNumber){
        setUpVerificatonCallbacks();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(verificationCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                            signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        iosDialog.cancel();
                        Log.d("responce",e.toString());
                        Toast.makeText(LoginFormActivity.this, getResources().getString(R.string.ernumber), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;
                        String send = getResources().getString(R.string.otprav) + " " + phoneNumber;
                        sendTo.setText(send);
                        iosDialog.cancel();
                        viewFlipper.setInAnimation(LoginFormActivity.this, R.anim.in_from_right);
                        viewFlipper.setOutAnimation(LoginFormActivity.this, R.anim.out_to_left);
                        viewFlipper.setDisplayedChild(1);
                    }
                };
    }


    public void verifyCode(View view) {
    String code=""+ numOne.getText().toString()+ numTwo.getText().toString()+ numThree.getText().toString()+ numFour.getText().toString()+ numFive.getText().toString()+ numSix.getText().toString();
    if(!code.equals("")){
        iosDialog.show();
    PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }else {
        Toast.makeText(this, getResources().getString(R.string.erver), Toast.LENGTH_SHORT).show();
    }

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Getuser();
                    } else {
                        if (task.getException() instanceof
                                FirebaseAuthInvalidCredentialsException) {
                            iosDialog.cancel();
                        }
                    }
                });
    }


    public void resendCode(View view) {

       setUpVerificatonCallbacks();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(verificationCallbacks)
                        .setForceResendingToken(resendToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }




    private void Getuser() {
        iosDialog.show();
        final String phone_no=phoneNumber.replace("+","");
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.USERDATA + "&userid=" + phone_no, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);

                    iosDialog.cancel();
                    try {
                        JSONObject jsonObject=new JSONObject(respo);
                        String code=jsonObject.optString("code");
                        if(code.equals("200")){

                            JSONArray msg=jsonObject.getJSONArray("msg");
                            JSONObject userdata=msg.getJSONObject(0);

                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            String who = userdata.optString("agent");
                            if(who.equals("1")) {
                                editor.putString(Constants.uid,userdata.optString("idd"));
                            } else {
                                editor.putString(Constants.uid, phone_no);
                            }
                            editor.putString(Constants.f_name,userdata.optString("fullname"));
                            editor.putString(Constants.u_pic,userdata.optString("imageprofile"));
                            editor.putString(Constants.agent,userdata.optString("agent"));
                            editor.putString("tel_agent",userdata.optString("tel_agent"));
                            BaseApp.saveIsLogin(true);
                            Intent intent = new Intent(LoginFormActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            editor.apply();
                            enable_location();
                        } else {
                            Intent intent=new Intent(LoginFormActivity.this, RegisterActivity.class);
                            intent.putExtra("number",phone_no);
                            startActivity(intent);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                            finish();
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, error -> {
                    Log.d("respo",error.toString());
                    iosDialog.cancel();
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.getCache().clear();

        rq.add(jsonObjectRequest);
    }

    private void enable_location() {
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}
