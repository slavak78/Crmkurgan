package ru.crmkurgan.main.Utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;

import ru.crmkurgan.main.Fragment.ChatFragment;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Activity.MainActivity;
import ru.crmkurgan.main.R;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class SendAudio {


    DatabaseReference rootref;
    String senderid;
    String Receiverid;
    String Receiver_name;
    String Receiver_pic;
    Context context;
    boolean ismatch_exits;

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;

    private final DatabaseReference Adduser_to_inbox;

    EditText message_field;


    public SendAudio(Context context, EditText message_field,
                     DatabaseReference rootref, DatabaseReference adduser_to_inbox
            , String senderid, String receiverid,String receiver_name,String receiver_pic) {

        this.context=context;
        this.message_field=message_field;
        this.rootref=rootref;
        this.Adduser_to_inbox=adduser_to_inbox;
        this.senderid=senderid;
        this.Receiverid=receiverid;
        this.Receiver_name=receiver_name;
        this.Receiver_pic=receiver_pic;
        mFileName = context.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";

    }



    private void startRecording() {

        if(mRecorder!=null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder=null;
        }

        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        if(mRecorder!=null)
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        if(mRecorder!=null)
            mRecorder.setOutputFile(mFileName);

        if(mRecorder!=null)
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            if(mRecorder!=null)
                mRecorder.prepare();
        } catch (IOException e) {
            Log.e("resp", "prepare() failed");
        }
        if(mRecorder!=null)
            mRecorder.start();


    }


    public void stopRecording() {
        stop_timer_without_recoder();
        if(mRecorder!=null ) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder=null;
            Runbeep("stop");
            UploadAudio();
        }
    }

    Handler handler;
    Runnable runnable;
    public void Runbeep(final String action){
        handler=new Handler();
        if(action.equals("start")) {
            message_field.setText(context.getResources().getString(R.string.timer));
            runnable = this::start_timer;

            handler.postDelayed(runnable, 700);
        }

        final MediaPlayer beep = MediaPlayer.create(context, R.raw.beep);
        beep.setVolume(100,100);
        beep.start();
        beep.setOnCompletionListener(mp -> {
            beep.release();
            if(action.equals("start"))
                startRecording();
        });
    }

    public void UploadAudio(){

        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constants.df.format(c);

        StorageReference reference= FirebaseStorage.getInstance().getReference();
        DatabaseReference dref=rootref.child("chat").child(senderid+"-"+Receiverid).push();
        final String key=dref.getKey();
        ChatFragment.uploadingAudioId =key;
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        HashMap<Object, Object> my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id",key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type","audio");
        my_dummi_pic_map.put("pic_url","none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", MainActivity.user_name);
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        rootref.updateChildren(dummy_push);



        Uri uri = Uri.fromFile(new File(mFileName));


        reference.child("Audio").child(key+".mp3").putFile(uri).addOnSuccessListener(taskSnapshot -> {

            ChatFragment.uploadingAudioId ="none";

            HashMap<Object, Object> message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", Receiverid);
            message_user_map.put("sender_id", senderid);
            message_user_map.put("chat_id",key);
            message_user_map.put("text", "");
            message_user_map.put("type","audio");
            reference.child("Audio").child(key+".mp3").getDownloadUrl().addOnSuccessListener(uri1 -> {
                message_user_map.put("pic_url", uri1.toString());
                message_user_map.put("status", "0");
                message_user_map.put("time", "");
                message_user_map.put("sender_name", MainActivity.user_name);
                message_user_map.put("timestamp", formattedDate);

                HashMap user_map = new HashMap<>();

                user_map.put(current_user_ref + "/" + key, message_user_map);
                user_map.put(chat_user_ref + "/" + key, message_user_map);

                rootref.updateChildren(user_map, (databaseError, databaseReference) -> {
                    String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                    String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

                    HashMap<Object, Object> sendermap=new HashMap<>();
                    sendermap.put("rid",senderid);
                    sendermap.put("name", MainActivity.user_name);
                    sendermap.put("pic", MainActivity.image);
                    sendermap.put("msg",context.getResources().getString(R.string.golos));
                    sendermap.put("status","0");
                    sendermap.put("date",formattedDate);
                    sendermap.put("timestamp", -1*System.currentTimeMillis());

                    HashMap<Object, Object> receivermap=new HashMap<>();
                    receivermap.put("rid",Receiverid);
                    receivermap.put("name",Receiver_name);
                    receivermap.put("pic",Receiver_pic);
                    receivermap.put("msg",context.getResources().getString(R.string.golos));
                    receivermap.put("status","1");
                    receivermap.put("date",formattedDate);
                    receivermap.put("timestamp", -1*System.currentTimeMillis());

                    HashMap both_user_map = new HashMap<>();
                    both_user_map.put(inbox_sender_ref , receivermap);
                    both_user_map.put(inbox_receiver_ref , sendermap);

                    Adduser_to_inbox.updateChildren(both_user_map).addOnCompleteListener((OnCompleteListener<Void>) task -> ChatFragment.SendPushNotification(rootref, MainActivity.user_name,context.getResources().getString(R.string.golos),
                            MainActivity.image,
                            ChatFragment.token,Receiverid,senderid));
                });
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = task.getResult();
                            SendWithToken(token,context.getResources().getString(R.string.golos));
                        });
            });
        });

    }

    public void SendWithToken(String tok, String message) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("device_token", tok);
            parameters.put("receiverid", Receiverid);
            parameters.put("senderid", senderid);
            parameters.put("title", MainActivity.user_name);
            parameters.put("icon", MainActivity.image);
            parameters.put("body", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SENDMESSAGE, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                }, error -> Log.d("respo",error.toString()));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    CountDownTimer timer;
    public void start_timer() {

        timer=new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long time=30000-millisUntilFinished;

                int min = (int) (time/1000) / 60;
                int sec = (int) (time/1000) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
                message_field.setText(timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                stopRecording();
                message_field.setText(null);
            }
        };

        timer.start();
    }

    public void stop_timer(){

        if(mRecorder!=null) {
            try {
                mRecorder.stop();
            } catch (Exception ignored) {

            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder=null;
        }

        if(handler!=null && runnable!=null)
            handler.removeCallbacks(runnable);

        message_field.setText(null);

        if(timer!=null){
            timer.cancel();
            message_field.setText(null);
        }

    }

    public void stop_timer_without_recoder(){

        if(handler!=null && runnable!=null)
            handler.removeCallbacks(runnable);

        message_field.setText(null);

        if(timer!=null){
            timer.cancel();
            message_field.setText(null);
        }

    }

}

