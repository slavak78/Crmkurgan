package ru.crmkurgan.main.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import ru.crmkurgan.main.Models.AboutModels;
import ru.crmkurgan.main.Utils.NetworkUtils;
import ru.crmkurgan.main.Utils.SendAudio;
import ru.crmkurgan.main.Item.ItemChat;
import ru.crmkurgan.main.Constants.Functions;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Activity.MainActivity;
import ru.crmkurgan.main.R;
import downloader.Error;
import downloader.OnDownloadListener;
import downloader.PRDownloader;
import iosdialog.IOSDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import ru.crmkurgan.main.Models.ChatModels;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChatFragment extends Fragment {

    DatabaseReference reference;
    String senderid = "";
    String Receiverid = "";
    String Receiver_name="";
    String Receiver_pic="null";
    public static String token="null";
    TextView typingIndicator;
    EmojiconEditText message;
    AboutModels modelAbout;

    private DatabaseReference adduserToInbox;
    private DatabaseReference sendTypingIndication;

    RecyclerView chatrecyclerview;
    TextView userName;
    private final List<ChatModels> mChats=new ArrayList<>();
    ItemChat mAdapter;
    ProgressBar progressBar;

    Query queryGetchat;
    Query myBlockStatusQuery;
    Query otherBlockStatusQuery;
    boolean isUserAlreadyBlock = false;

    ImageView profileimage;
    public static String senderidForCheckNotification = "";
    public static String uploadingImageId = "none";

    Context context;
    IOSDialog loadingView;
    View getView;
    LinearLayout takephoto, llcamera, opengallery;
    ImageView sendbtn, camerabtn;
    ImageButton block, micBtn;

    public static String uploadingAudioId = "none";

    File direct;

    SendAudio sendAudio;
    ActivityResultLauncher<String> mPermissionCamera, mPermissionWrite,  mPermissionAudio, mPermissionRead;
    ActivityResultLauncher<Intent> mResult1, mResult2;
    final long[] touchtime = {System.currentTimeMillis()};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getView = inflater.inflate(R.layout.fragment_chat, container, false);

        mPermissionCamera = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        openCameraIntent();
                    }
                });

        mPermissionAudio = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        mPermissionWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                });

        mPermissionWrite = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        Toast.makeText(context, getResources().getString(R.string.clicks1), Toast.LENGTH_LONG).show();
                    }
                });

        mPermissionRead = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        dopick();
                    }
                });

        mResult1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getResultCode() == RESULT_OK) {
                    Matrix matrix = new Matrix();
                    try {
                        ExifInterface exif = new ExifInterface(imageFilePath);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));

                    InputStream imageStream = null;
                    try {
                        imageStream = requireActivity().getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                    UploadImage(baos);
                }
                }
        );



        mResult2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if(result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        InputStream imageStream = null;
                        try {
                            imageStream = requireActivity().getContentResolver().openInputStream(selectedImage);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

                        String path = getPath(selectedImage);
                        Matrix matrix = new Matrix();
                        ExifInterface exif;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            try {
                                exif = new ExifInterface(path);
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        matrix.postRotate(90);
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        matrix.postRotate(180);
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        matrix.postRotate(270);
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        UploadImage(baos);
                    }
                }
        );


        modelAbout = new AboutModels();
        context = getContext();
        if (NetworkUtils.isConnected(getActivity())) {
            getSetting();
        } else {
            Toast.makeText(context, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
        }
        direct = new File(Environment.getExternalStorageDirectory() +"/BaseCrmkurgan/");

        reference = FirebaseDatabase.getInstance().getReference();
        adduserToInbox =FirebaseDatabase.getInstance().getReference();

        userName = getView.findViewById(R.id.fullname);
        profileimage = getView.findViewById(R.id.profileimage);
        block = getView.findViewById(R.id.block);
        progressBar = getView.findViewById(R.id.progress_bar);
        chatrecyclerview = getView.findViewById(R.id.chatlist);
        sendbtn = getView.findViewById(R.id.sendbtn);
        takephoto = getView.findViewById(R.id.takephoto);
        opengallery = getView.findViewById(R.id.opengallery);
        llcamera = getView.findViewById(R.id.llcamera);
        camerabtn = getView.findViewById(R.id.uploadimagebtn);
        micBtn =  getView.findViewById(R.id.mic_btn);

        message = getView.findViewById(R.id.msgedittext);
        message.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.toString().trim().length()==0) {
                    sendbtn.setVisibility(View.GONE);
                    micBtn.setVisibility(View.VISIBLE);
                }else  {
                    sendbtn.setVisibility(View.VISIBLE);
                    micBtn.setVisibility(View.GONE);
                }
            }
        });



        ImageView Emoji = getView.findViewById(R.id.emoticon);
        EmojiconEditText emojiconEditText = getView.findViewById(R.id.msgedittext);
        EmojIconActions emojIcon = new EmojIconActions(getActivity(), getView, emojiconEditText, Emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

            }
            @Override
            public void onKeyboardClose() {
            }
        });
        block.setOnClickListener(v -> {

            if(isUserAlreadyBlock)
                unblock();
            else
                block();
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            senderid = bundle.getString("Sender_Id");
            Receiverid = bundle.getString("Receiver_Id");
            Receiver_name=bundle.getString("name");
            Receiver_pic=bundle.getString("picture");
            userName.setText(Receiver_name);
            senderidForCheckNotification =Receiverid;

            if (Receiver_pic.isEmpty()) {
                Picasso.get().load(Constants.BASEURL+"/users.png").fit().centerCrop()
                        .into(profileimage);
            } else {
                Picasso.get().load(Receiver_pic).fit().centerCrop()
                        .placeholder(R.drawable.image_placeholder)
                        .into(profileimage);
            }


            sendAudio =new SendAudio(context,message, reference, adduserToInbox,
                    senderid,Receiverid,Receiver_name,Receiver_pic);

            reference.child("Users").child(Receiverid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                        token= Objects.requireNonNull(dataSnapshot.child("token").getValue()).toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        loadingView = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        final LinearLayoutManager layout = new LinearLayoutManager(context);
        layout.setStackFromEnd(false);
        chatrecyclerview.setLayoutManager(layout);
        chatrecyclerview.setHasFixedSize(false);
        OverScrollDecoratorHelper.setUpOverScroll(chatrecyclerview, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        mAdapter = new ItemChat(mChats, senderid, context, (item, v) -> {

            if(item.getType().equals("image"))
                OpenfullsizeImage(item);

            if(v.getId()==R.id.audiobubble){
                RelativeLayout mainlayout=(RelativeLayout) v.getParent();

                File fullpath = new File(Environment.getExternalStorageDirectory() +"/BaseCrmkurgan/"+item.getChat_id()+".mp3");
                if(fullpath.exists()) {

                    OpenAudio(fullpath.getAbsolutePath());

                }else {
                    download_audio(mainlayout.findViewById(R.id.progress),item);
                }

            }


        }, (item, view) -> {
            if (view.getId() == R.id.msgtxt) {
                if(senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                    delete_Message(item);
            } else if (view.getId() == R.id.chatimage) {
                if(senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                    delete_Message(item);
            }else if (view.getId() == R.id.audiobubble) {
                if(senderid.equals(item.getSender_id()) && istodaymessage(item.getTimestamp()))
                    delete_Message(item);
            }
        });

        chatrecyclerview.setAdapter(mAdapter);
        chatrecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean userScrolled;
            int scrollOutitems;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollOutitems = layout.findFirstCompletelyVisibleItemPosition();

                if (userScrolled && (scrollOutitems == 0 && mChats.size()>9)) {
                    userScrolled = false;
                    loadingView.show();
                    reference.child("chat").child(senderid + "-" + Receiverid).orderByChild("chat_id")
                            .endAt(mChats.get(0).getChat_id()).limitToLast(20)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<ChatModels> arrayList=new ArrayList<>();
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        ChatModels item=snapshot.getValue(ChatModels.class);
                                        arrayList.add(item);
                                    }
                                    for (int i=arrayList.size()-2; i>=0; i-- ){
                                        mChats.add(0,arrayList.get(i));
                                    }

                                    mAdapter.notifyDataSetChanged();
                                    loadingView.cancel();

                                    if(arrayList.size()>8){
                                        chatrecyclerview.scrollToPosition(arrayList.size());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

        sendbtn.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(message.getText().toString())){
                    SendMessage(message.getText().toString());
                    message.setText(null);
            }
        });
        takephoto.setOnClickListener(v -> {
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        mPermissionCamera.launch(Manifest.permission.CAMERA);
                    } else {
                        openCameraIntent();
                    }
        });

        opengallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                mPermissionRead.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                dopick();
            }
        });

        camerabtn.setOnClickListener(v -> {
            if(llcamera.getVisibility()==View.VISIBLE){
                closecameralayout();
            }else{
                opencameralayout();
            }
        });

        getView.findViewById(R.id.Goback).setOnClickListener(v -> {
            Functions.hideSoftKeyboard(requireActivity());
            requireActivity().onBackPressed();
        });

        message.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                SendTypingIndicator(false);
            }
        });


        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SendTypingIndicator(count != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        micBtn.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (((ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) && (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        mPermissionAudio.launch(Manifest.permission.RECORD_AUDIO);
                    }
                    if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        mPermissionWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
            } else {
                    sendA();
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (touchtime[0] + 4000 > System.currentTimeMillis()) {
                    sendAudio.stop_timer();
                    Toast.makeText(context, getResources().getString(R.string.hold), Toast.LENGTH_SHORT).show();
                } else {
                    sendAudio.stopRecording();
                    Toast.makeText(context, getResources().getString(R.string.stop), Toast.LENGTH_SHORT).show();
                }

            }
            return false;
        });
        ReceivetypeIndication();

        getChat();
        return getView;
    }

    ValueEventListener valueEventListener;
    ChildEventListener eventListener;
    ValueEventListener my_inbox_listener;
    ValueEventListener other_inbox_listener;

    public void getChat() {
        mChats.clear();
        DatabaseReference mchatRefReteriving = FirebaseDatabase.getInstance().getReference();
        queryGetchat = mchatRefReteriving.child("chat").child(senderid + "-" + Receiverid);

        myBlockStatusQuery = mchatRefReteriving.child("Inbox")
                .child(MainActivity.user_id)
                .child(Receiverid);

        otherBlockStatusQuery = mchatRefReteriving.child("Inbox")
                .child(Receiverid)
                .child(MainActivity.user_id);

        eventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                try {
                    ChatModels model = dataSnapshot.getValue(ChatModels.class);
                    mChats.add(model);
                    mAdapter.notifyDataSetChanged();
                    chatrecyclerview.scrollToPosition(mChats.size() - 1);
                }
                catch (Exception ex) {
                    Log.e("", ex.getMessage());
                }
                ChangeStatus();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot.getValue() != null) {

                    try {
                        ChatModels model = dataSnapshot.getValue(ChatModels.class);

                        for (int i=mChats.size()-1;i>=0;i--){
                            if(mChats.get(i).getTimestamp().equals(dataSnapshot.child("timestamp").getValue())){
                                mChats.remove(i);
                                mChats.add(i,model);
                                break;
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    catch (Exception ex) {
                        Log.e("", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("", databaseError.getMessage());
            }
        };

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                queryGetchat.removeEventListener(valueEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        my_inbox_listener =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.child("block").getValue()!=null){
                    String block= Objects.requireNonNull(dataSnapshot.child("block").getValue()).toString();
                    if(block.equals("1")){
                        getView.findViewById(R.id.writechatlayout).setVisibility(View.INVISIBLE);
                    }else {
                        getView.findViewById(R.id.writechatlayout).setVisibility(View.VISIBLE);
                    }
                }else {
                    getView.findViewById(R.id.writechatlayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        other_inbox_listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.child("block").getValue()!=null){
                    String block= Objects.requireNonNull(dataSnapshot.child("block").getValue()).toString();
                    isUserAlreadyBlock = block.equals("1");
                }else {
                    isUserAlreadyBlock =false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        queryGetchat.limitToLast(20).addChildEventListener(eventListener);
        mchatRefReteriving.child("chat").addValueEventListener(valueEventListener);

        myBlockStatusQuery.addValueEventListener(my_inbox_listener);
        otherBlockStatusQuery.addValueEventListener(other_inbox_listener);
    }

    public void sendA() {
        touchtime[0] = System.currentTimeMillis();
        sendAudio.Runbeep("start");
        Toast.makeText(context, getResources().getString(R.string.record), Toast.LENGTH_SHORT).show();
    }

    public void SendMessage(final String message) {
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constants.df.format(c);

        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        DatabaseReference reference = this.reference.child("chat").child(senderid + "-" + Receiverid).push();
        final String pushid = reference.getKey();
        final HashMap<Object, Object> message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", Receiverid);
        message_user_map.put("sender_id", senderid);
        message_user_map.put("chat_id",pushid);
        message_user_map.put("text", message);
        message_user_map.put("type","text");
        message_user_map.put("pic_url","");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", MainActivity.user_name);
        message_user_map.put("timestamp", formattedDate);

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + pushid, message_user_map);
        user_map.put(chat_user_ref + "/" + pushid, message_user_map);

        this.reference.updateChildren(user_map, (databaseError, databaseReference) -> {
            String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
            String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

            HashMap<Object, Object> sendermap=new HashMap<>();
            sendermap.put("rid",senderid);
            sendermap.put("name", MainActivity.user_name);
            sendermap.put("pic", MainActivity.image);
            sendermap.put("msg",message);
            sendermap.put("status","0");
            sendermap.put("timestamp", -1*System.currentTimeMillis());
            sendermap.put("date",formattedDate);

            HashMap<Object, Object> receivermap=new HashMap<>();
            receivermap.put("rid",Receiverid);
            receivermap.put("name",Receiver_name);
            receivermap.put("pic",Receiver_pic);
            receivermap.put("msg",message);
            receivermap.put("status","1");
            receivermap.put("timestamp", -1*System.currentTimeMillis());
            receivermap.put("date",formattedDate);

            HashMap both_user_map = new HashMap<>();
            both_user_map.put(inbox_sender_ref , receivermap);
            both_user_map.put(inbox_receiver_ref , sendermap);

            adduserToInbox.updateChildren(both_user_map).addOnCompleteListener((OnCompleteListener<Void>) task -> ChatFragment.SendPushNotification(ChatFragment.this.reference, MainActivity.user_name,message,
                    MainActivity.image,
                    token,Receiverid,senderid));
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    SendWithToken(token,message);
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
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
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

    public void UploadImage(ByteArrayOutputStream byteArrayOutputStream){
        byte[] data = byteArrayOutputStream.toByteArray();
        Date c = Calendar.getInstance().getTime();
        final String formattedDate = Constants.df.format(c);

        StorageReference reference= FirebaseStorage.getInstance().getReference();
        DatabaseReference dref= this.reference.child("chat").child(senderid+"-"+Receiverid).push();
        final String key=dref.getKey();
        uploadingImageId = key;
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;

        HashMap<Object, Object> my_dummi_pic_map = new HashMap<>();
        my_dummi_pic_map.put("receiver_id", Receiverid);
        my_dummi_pic_map.put("sender_id", senderid);
        my_dummi_pic_map.put("chat_id",key);
        my_dummi_pic_map.put("text", "");
        my_dummi_pic_map.put("type","image");
        my_dummi_pic_map.put("pic_url","none");
        my_dummi_pic_map.put("status", "0");
        my_dummi_pic_map.put("time", "");
        my_dummi_pic_map.put("sender_name", MainActivity.user_name);
        my_dummi_pic_map.put("timestamp", formattedDate);

        HashMap dummy_push = new HashMap<>();
        dummy_push.put(current_user_ref + "/" + key, my_dummi_pic_map);
        this.reference.updateChildren(dummy_push);

        reference.child("images").child(key+".jpg").putBytes(data).addOnSuccessListener(taskSnapshot -> {

            uploadingImageId ="none";

            HashMap<Object, Object> message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", Receiverid);
            message_user_map.put("sender_id", senderid);
            message_user_map.put("chat_id",key);
            message_user_map.put("text", "");
            message_user_map.put("type","image");
            reference.child("images").child(key+".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                String hh = uri.toString();
                message_user_map.put("pic_url", hh);
                message_user_map.put("status", "0");
                message_user_map.put("time", "");
                message_user_map.put("sender_name", MainActivity.user_name);
                message_user_map.put("timestamp", formattedDate);

                HashMap user_map = new HashMap<>();

                user_map.put(current_user_ref + "/" + key, message_user_map);
                user_map.put(chat_user_ref + "/" + key, message_user_map);
                ChatFragment.this.reference.updateChildren(user_map, (databaseError, databaseReference) -> {
                    String inbox_sender_ref = "Inbox" + "/" + senderid + "/" + Receiverid;
                    String inbox_receiver_ref = "Inbox" + "/" + Receiverid + "/" + senderid;

                    HashMap<Object, Object> sendermap = new HashMap<>();
                    sendermap.put("rid", senderid);
                    sendermap.put("name", MainActivity.user_name);
                    sendermap.put("pic", MainActivity.image);
                    sendermap.put("msg", getResources().getString(R.string.photo));
                    sendermap.put("status", "0");
                    sendermap.put("timestamp", -1 * System.currentTimeMillis());
                    sendermap.put("date", formattedDate);

                    HashMap<Object, Object> receivermap = new HashMap<>();
                    receivermap.put("rid", Receiverid);
                    receivermap.put("name", Receiver_name);
                    receivermap.put("pic", Receiver_pic);
                    receivermap.put("msg", getResources().getString(R.string.photo));
                    receivermap.put("status", "1");
                    receivermap.put("timestamp", -1 * System.currentTimeMillis());
                    receivermap.put("date", formattedDate);

                    HashMap both_user_map = new HashMap<>();
                    both_user_map.put(inbox_sender_ref, receivermap);
                    both_user_map.put(inbox_receiver_ref, sendermap);

                    adduserToInbox.updateChildren(both_user_map).addOnCompleteListener((OnCompleteListener<Void>) task -> ChatFragment.SendPushNotification(ChatFragment.this.reference, MainActivity.user_name, getResources().getString(R.string.photo),
                            MainActivity.image,
                            token, Receiverid, senderid));

                });
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = task.getResult();
                            SendWithToken(token,getResources().getString(R.string.photo));
                        });
            });

        });

    }

    public void ChangeStatus(){
        final Date c = Calendar.getInstance().getTime();
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final Query query1 = reference.child("chat").child(Receiverid+"-"+senderid).orderByChild("status").equalTo("0");
        final Query query2 = reference.child("chat").child(senderid+"-"+Receiverid).orderByChild("status").equalTo("0");

        final DatabaseReference inbox_change_status_1=reference.child("Inbox").child(senderid+"/"+Receiverid);
        final DatabaseReference inbox_change_status_2=reference.child("Inbox").child(Receiverid+"/"+senderid);

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if(!Objects.equals(nodeDataSnapshot.child("sender_id").getValue(), senderid)){
                        String key = nodeDataSnapshot.getKey();
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time",sdf.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nodeDataSnapshot : dataSnapshot.getChildren()) {
                    if(!Objects.equals(nodeDataSnapshot.child("sender_id").getValue(), senderid)){
                        String key = nodeDataSnapshot.getKey();
                        String path = "chat" + "/" + dataSnapshot.getKey() + "/" + key;
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        result.put("time",sdf.format(c));
                        reference.child(path).updateChildren(result);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        inbox_change_status_1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(Objects.equals(dataSnapshot.child("rid").getValue(), Receiverid)){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_1.updateChildren(result);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        inbox_change_status_2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(Objects.equals(dataSnapshot.child("rid").getValue(), Receiverid)){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("status", "1");
                        inbox_change_status_2.updateChildren(result);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void download_audio(final ProgressBar p_bar, ChatModels item){
        p_bar.setVisibility(View.VISIBLE);
        PRDownloader.download(item.getPic_url(), direct.getPath(), item.getChat_id()+".mp3")
                .build()
                .setOnStartOrResumeListener(() -> {

                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {

                })
                .setOnProgressListener(progress -> {

                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        p_bar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onError(Error error) {

                    }
                });

    }


    public void dopick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        mResult2.launch(intent);
    }
    public void OpenAudio(String path){
        PlayAudioFragment play_audio_fragment = new PlayAudioFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("path", path);
        play_audio_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, play_audio_fragment).commit();

    }

    private void delete_Message(final ChatModels chat_models) {

        final CharSequence[] options = { getResources().getString(R.string.delmes), getResources().getString(R.string.otmena) };

        AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.AlertDialog);

        builder.setTitle(null);

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals(getResources().getString(R.string.delmes)))

            {
                update_message(chat_models);

            }


            else if (options[item].equals(getResources().getString(R.string.otmena))) {

                dialog.dismiss();

            }

        });

        builder.show();

    }

    public void update_message(ChatModels item){
        final String current_user_ref = "chat" + "/" + senderid + "-" + Receiverid;
        final String chat_user_ref = "chat" + "/" + Receiverid + "-" + senderid;


        final HashMap<Object, Object> message_user_map = new HashMap<>();
        message_user_map.put("receiver_id", item.getReceiver_id());
        message_user_map.put("sender_id", item.getSender_id());
        message_user_map.put("chat_id",item.getChat_id());
        message_user_map.put("text", "Delete this message");
        message_user_map.put("type","delete");
        message_user_map.put("pic_url","");
        message_user_map.put("status", "0");
        message_user_map.put("time", "");
        message_user_map.put("sender_name", MainActivity.user_name);
        message_user_map.put("timestamp", item.getTimestamp());

        final HashMap user_map = new HashMap<>();
        user_map.put(current_user_ref + "/" + item.getChat_id(), message_user_map);
        user_map.put(chat_user_ref + "/" + item.getChat_id(), message_user_map);

        reference.updateChildren(user_map, (databaseError, databaseReference) -> {

        });

    }

    public void Block_user(){
        reference.child("Inbox")
                .child(Receiverid)
                .child(MainActivity.user_id).child("block").setValue("1");
        Toast.makeText(context, getResources().getString(R.string.userblocked), Toast.LENGTH_SHORT).show();

    }

    public void UnBlock_user(){
        reference.child("Inbox")
                .child(Receiverid)
                .child(MainActivity.user_id).child("block").setValue("0");
        Toast.makeText(context, getResources().getString(R.string.userunblocked), Toast.LENGTH_SHORT).show();

    }

    public boolean istodaymessage(String date) {
        Calendar cal = Calendar.getInstance();
        int today_day = cal.get(Calendar.DAY_OF_MONTH);
        long currenttime = System.currentTimeMillis();

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ", Locale.getDefault());
        long databasedate = 0;
        Date d;
        try {
            d = f.parse(date);
            assert d != null;
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = currenttime - databasedate;
        if (difference < 86400000) {
            int chatday = Integer.parseInt(date.substring(0, 2));
            return today_day == chatday;
        }

        return false;
    }

    private boolean check_ReadStoragepermission(){
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                mPermissionRead.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private boolean check_writeStoragepermission(){
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                mPermissionRead.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(requireActivity().getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(), "ru.crmkurgan.main.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mResult1.launch(pictureIntent);
            }
        }
    }

    String imageFilePath;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public  String getPath(Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = getResources().getString(R.string.notfound1);
        }
        return result;
    }

    public void SendTypingIndicator(boolean indicate){
        if(indicate){
            final HashMap<Object, Object> message_user_map = new HashMap<>();
            message_user_map.put("receiver_id", Receiverid);
            message_user_map.put("sender_id", senderid);

            sendTypingIndication =FirebaseDatabase.getInstance().getReference().child("typing_indicator");
            sendTypingIndication.child(senderid+"-"+Receiverid).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    sendTypingIndication.child(Receiverid+"-"+senderid).setValue(message_user_map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                }
            });
        }

        else {
            sendTypingIndication =FirebaseDatabase.getInstance().getReference().child("typing_indicator");

            sendTypingIndication.child(senderid+"-"+Receiverid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    sendTypingIndication.child(Receiverid+"-"+senderid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });

                }
            });

        }

    }


    public void ReceivetypeIndication(){
        typingIndicator = getView.findViewById(R.id.typeindicator);

        DatabaseReference receiveTypingIndication = FirebaseDatabase.getInstance().getReference().child("typing_indicator");
        receiveTypingIndication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Receiverid+"-"+senderid).exists()){
                    String receiver= String.valueOf(dataSnapshot.child(Receiverid+"-"+senderid).child("sender_id").getValue());
                    if(receiver.equals(Receiverid)){
                        typingIndicator.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    typingIndicator.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadingImageId ="none";
        SendTypingIndicator(false);
        queryGetchat.removeEventListener(eventListener);
        myBlockStatusQuery.removeEventListener(my_inbox_listener);
        otherBlockStatusQuery.removeEventListener(other_inbox_listener);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        uploadingImageId ="none";
        SendTypingIndicator(false);
        queryGetchat.removeEventListener(eventListener);
        myBlockStatusQuery.removeEventListener(my_inbox_listener);
        otherBlockStatusQuery.removeEventListener(other_inbox_listener);
    }

    public void OpenfullsizeImage(ChatModels item){
        FullImageFragment see_image_f = new FullImageFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", item.getPic_url());
        args.putSerializable("chat_id", item.getChat_id());
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Chat_F, see_image_f).commit();

    }

    public void closecameralayout(){
        camerabtn.setColorFilter(context.getResources().getColor(R.color.gray));
        llcamera.setVisibility(View.GONE);
    }


    public void opencameralayout(){
        llcamera.setVisibility(View.VISIBLE);
        camerabtn.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
    }

    public static void SendPushNotification(DatabaseReference rootref,
                                            String name,String message,
                                            String picture,String token,
                                            String receiverid,String senderid){

        Map<String,String> notimap= new HashMap<>();
        notimap.put("name",name);
        notimap.put("message",message);
        notimap.put("picture",picture);
        notimap.put("token",token);
        notimap.put("receiverid", receiverid);
        notimap.put("action_type","message");
        rootref.child("notifications").child(senderid).push().setValue(notimap);


    }




    public void block() {
        new AlertDialog.Builder(requireActivity(), R.style.AlertDialog)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.questblock))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Block_user();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void unblock() {
        new AlertDialog.Builder(requireActivity(), R.style.AlertDialog)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.questunblock))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    UnBlock_user();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void getSetting() {
        JSONObject parameters = new JSONObject();
        RequestQueue rq = Volley.newRequestQueue(requireActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Constants.SETTINGAPP, parameters, response -> {
                    String respo=response.toString();
                    Log.d("responce",respo);
                    getData(respo);
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
            if(code.equals("200")){
                JSONArray msg=jsonObject.getJSONArray("msg");
                JSONObject userdata=msg.getJSONObject(0);

                modelAbout.setGhiphy(userdata.getString("ghipy_api"));

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

}
