package ru.crmkurgan.main.Item;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Constants.Constants;
import ru.crmkurgan.main.Fragment.ChatFragment;
import ru.crmkurgan.main.Models.ChatModels;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatModels> mDataSet;
    String myID;
    private static final int mychat = 1;
    private static final int friendchat = 2;
    private static final int mychatimage = 3;
    private static final int otherchatimage = 4;
    private static final int alert_message = 7;

    private static final int my_audio_message = 8;
    private static final int other_audio_message = 9;



    Context context;
    Integer today_day;

    private final ItemChat.OnItemClickListener listener;
    private final ItemChat.OnLongClickListener long_listener;

    public interface OnItemClickListener {
        void onItemClick(ChatModels item, View view);
    }

    public interface OnLongClickListener {
        void onLongclick (ChatModels item, View view);
    }

    public ItemChat(List<ChatModels> dataSet, String id, Context context, ItemChat.OnItemClickListener listener, ItemChat.OnLongClickListener long_listener) {
        mDataSet = dataSet;
        this.myID=id;
        this.context=context;
        this.listener = listener;
        this.long_listener=long_listener;
        Calendar cal = Calendar.getInstance();
        today_day = cal.get(Calendar.DAY_OF_MONTH);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        View v;
        switch (viewtype){
            case mychat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_my, viewGroup, false);
                return new Chatviewholder(v);
            case friendchat:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_other, viewGroup, false);
                return new Chatviewholder(v);
            case mychatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_my, viewGroup, false);
                return new Chatimageviewholder(v);
            case otherchatimage:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_image_other, viewGroup, false);
                return new Chatimageviewholder(v);

            case my_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chataudio, viewGroup, false);
                return new Chataudioviewholder(v);

            case other_audio_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_audio_other, viewGroup, false);
                return new Chataudioviewholder(v);
            case alert_message:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_alert, viewGroup, false);
                return new Alertviewholder(v);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModels chat = mDataSet.get(position);

        switch (chat.getType()) {
            case "text":
                Chatviewholder chatviewholder = (Chatviewholder) holder;
                if (chat.getSender_id().equals(myID)) {
                    if (chat.getStatus().equals("1")) {
                        String seen = context.getResources().getString(R.string.seen) + " " + chat.getTime();
                        chatviewholder.message_seen.setText(seen);
                    }
                    else
                        chatviewholder.message_seen.setText(context.getResources().getString(R.string.sent));

                } else {
                    chatviewholder.message_seen.setText("");
                }

                if (position != 0) {
                    ChatModels chat2 = mDataSet.get(position - 1);
                    if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                        chatviewholder.datetxt.setVisibility(View.GONE);
                    } else {
                        chatviewholder.datetxt.setVisibility(View.VISIBLE);
                        chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                    }
                    chatviewholder.message.setText(chat.getText());
                } else {
                    chatviewholder.datetxt.setVisibility(View.VISIBLE);
                    chatviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                    chatviewholder.message.setText(chat.getText());
                }

                chatviewholder.bind(chat, long_listener);

                break;
            case "image": {
                final Chatimageviewholder chatimageholder = (Chatimageviewholder) holder;
                if (chat.getSender_id().equals(myID)) {
                    if (chat.getStatus().equals("1")) {
                        String seen = context.getResources().getString(R.string.seen) + " " + chat.getTime();
                        chatimageholder.messageSeen.setText(seen);
                    }
                    else
                        chatimageholder.messageSeen.setText(context.getResources().getString(R.string.sent));

                } else {
                    chatimageholder.messageSeen.setText("");
                }
                if (chat.getPic_url().equals("none")) {
                    if (ChatFragment.uploadingImageId.equals(chat.getChat_id())) {
                        chatimageholder.progressBar.setVisibility(View.VISIBLE);
                        chatimageholder.messageSeen.setText("");
                    } else {
                        chatimageholder.progressBar.setVisibility(View.GONE);
                        chatimageholder.notSendMessageIcon.setVisibility(View.VISIBLE);
                        chatimageholder.messageSeen.setText(context.getResources().getString(R.string.notdelivered));
                    }
                } else {
                    chatimageholder.notSendMessageIcon.setVisibility(View.GONE);
                    chatimageholder.progressBar.setVisibility(View.GONE);
                }

                if (position != 0) {
                    ChatModels chat2 = mDataSet.get(position - 1);
                    if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                        chatimageholder.datetxt.setVisibility(View.GONE);
                    } else {
                        chatimageholder.datetxt.setVisibility(View.VISIBLE);
                        chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                    }
                } else {
                    chatimageholder.datetxt.setVisibility(View.VISIBLE);
                    chatimageholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                }
                Picasso.get().load(chat.getPic_url()).placeholder(R.drawable.image_placeholder).fit().centerCrop().into(chatimageholder.chatimage);

                chatimageholder.bind(mDataSet.get(position), listener, long_listener);
                break;
            }
            case "audio":
                final Chataudioviewholder chataudioviewholder = (Chataudioviewholder) holder;
                if (chat.getSender_id().equals(myID)) {
                    if (chat.getStatus().equals("1")) {
                        String seen = context.getResources().getString(R.string.seen) + " " + chat.getTime();
                        chataudioviewholder.messageSeen.setText(seen);
                    }
                    else
                        chataudioviewholder.messageSeen.setText(context.getResources().getString(R.string.sent));

                } else {
                    chataudioviewholder.messageSeen.setText("");
                }
                if (chat.getPic_url().equals("none")) {
                    if (ChatFragment.uploadingAudioId.equals(chat.getChat_id())) {
                        chataudioviewholder.progressBar.setVisibility(View.VISIBLE);
                        chataudioviewholder.messageSeen.setText("");
                    } else {
                        chataudioviewholder.progressBar.setVisibility(View.GONE);
                        chataudioviewholder.notSendMessageIcon.setVisibility(View.VISIBLE);
                        chataudioviewholder.messageSeen.setText(context.getResources().getString(R.string.notdelivered));
                    }
                } else {
                    chataudioviewholder.notSendMessageIcon.setVisibility(View.GONE);
                    chataudioviewholder.progressBar.setVisibility(View.GONE);
                }
                if (position != 0) {
                    ChatModels chat2 = mDataSet.get(position - 1);
                    if (chat2.getTimestamp().substring(14, 16).equals(chat.getTimestamp().substring(14, 16))) {
                        chataudioviewholder.datetxt.setVisibility(View.GONE);
                    } else {
                        chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                        chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                    }
                } else {
                    chataudioviewholder.datetxt.setVisibility(View.VISIBLE);
                    chataudioviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

                }

                chataudioviewholder.seekBar.setEnabled(false);

                File fullpath = new File(Environment.getExternalStorageDirectory() + "/BaseApp/" + chat.getChat_id() + ".mp3");
                if (fullpath.exists()) {
                    chataudioviewholder.totalTime.setText(getfileduration(Uri.parse(fullpath.getAbsolutePath())));

                } else {
                    chataudioviewholder.totalTime.setText(null);
                }


                chataudioviewholder.bind(mDataSet.get(position), listener, long_listener);

                break;
            case "delete":
                Alertviewholder alertviewholder = (Alertviewholder) holder;
                alertviewholder.message.setTextColor(context.getResources().getColor(R.color.gray));
                alertviewholder.message.setBackground(ContextCompat.getDrawable(context, R.drawable.round_edittext_background));
                String del = context.getResources().getString(R.string.messdel) + " " + chat.getSender_name();
                alertviewholder.message.setText(del);

                if (position != 0) {
                    ChatModels chat2 = mDataSet.get(position - 1);
                    if (chat2.getTimestamp().substring(11, 13).equals(chat.getTimestamp().substring(11, 13))) {
                        alertviewholder.datetxt.setVisibility(View.GONE);
                    } else {
                        alertviewholder.datetxt.setVisibility(View.VISIBLE);
                        alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));
                    }

                } else {
                    alertviewholder.datetxt.setVisibility(View.VISIBLE);
                    alertviewholder.datetxt.setText(ChangeDate(chat.getTimestamp()));

                }

                break;
        }


    }

    @Override
    public int getItemViewType(int position) {
        switch (mDataSet.get(position).getType()) {
            case "text":
                if (mDataSet.get(position).getSender_id().equals(myID)) {
                    return mychat;
                }
                return friendchat;
            case "image":
                if (mDataSet.get(position).getSender_id().equals(myID)) {
                    return mychatimage;
                }

                return otherchatimage;

            case "audio":
                if (mDataSet.get(position).getSender_id().equals(myID)) {
                    return my_audio_message;
                }
                return other_audio_message;
            default:
                return alert_message;
        }
    }

    static class Chatviewholder extends RecyclerView.ViewHolder {
        TextView message,datetxt,message_seen;
        View view;
        public Chatviewholder(View itemView) {
            super(itemView);
            view = itemView;
            this.message = view.findViewById(R.id.msgtxt);
            this.datetxt=view.findViewById(R.id.datetxt);
            message_seen=view.findViewById(R.id.messageseen);
        }

        public void bind(final ChatModels item, final ItemChat.OnLongClickListener long_listener) {
            message.setOnLongClickListener(v -> {
                long_listener.onLongclick(item,v);
                return false;
            });
        }
    }

    static class Chatimageviewholder extends RecyclerView.ViewHolder {
        ImageView chatimage;
        TextView datetxt, messageSeen;
        ProgressBar progressBar;
        ImageView notSendMessageIcon;
        View getView;
        public Chatimageviewholder(View itemView) {
            super(itemView);
            getView = itemView;
            this.chatimage = getView.findViewById(R.id.chatimage);
            this.datetxt= getView.findViewById(R.id.datetxt);
            messageSeen = getView.findViewById(R.id.messageseen);
            notSendMessageIcon = getView.findViewById(R.id.notsend);
            progressBar = getView.findViewById(R.id.progress);
        }

        public void bind(final ChatModels item, final ItemChat.OnItemClickListener listener, final ItemChat.OnLongClickListener long_listener) {

            chatimage.setOnClickListener(v -> listener.onItemClick(item,v));

            chatimage.setOnLongClickListener(v -> {
                long_listener.onLongclick(item,v);
                return false;
            });
        }

    }

    static class Chataudioviewholder extends RecyclerView.ViewHolder{
        TextView datetxt, messageSeen;
        ProgressBar progressBar;
        ImageView notSendMessageIcon;
        ImageView playBtn;
        SeekBar seekBar;
        TextView totalTime;
        LinearLayout audioBubble;
        View getView;

        public Chataudioviewholder(View itemView) {
            super(itemView);
            getView = itemView;
            audioBubble = getView.findViewById(R.id.audiobubble);
            datetxt = getView.findViewById(R.id.datetxt);
            messageSeen = getView.findViewById(R.id.messageseen);
            notSendMessageIcon = getView.findViewById(R.id.notsend);
            progressBar = getView.findViewById(R.id.progress);
            this.playBtn = getView.findViewById(R.id.playbtn);
            this.seekBar = getView.findViewById(R.id.seekbar);
            this.totalTime = getView.findViewById(R.id.totaltime);

        }

        public void bind(final ChatModels item, final ItemChat.OnItemClickListener listener, final ItemChat.OnLongClickListener long_listener) {



            audioBubble.setOnClickListener(v -> listener.onItemClick(item,v));

            audioBubble.setOnLongClickListener(v -> {
                long_listener.onLongclick(item,v);
                return false;
            });


        }


    }

    static class Alertviewholder extends RecyclerView.ViewHolder {
        TextView message, datetxt;
        View getView;
        public Alertviewholder(View itemView) {
            super(itemView);
            getView = itemView;
            this.message = getView.findViewById(R.id.message);
            this.datetxt = getView.findViewById(R.id.datetxt);
        }

    }

    public String ChangeDate(String date){
        long currenttime= System.currentTimeMillis();

        long databasedate = 0;
        Date d = null;
        try {
            d = Constants.df.parse(date);
            assert d != null;
            databasedate = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
       long difference=currenttime-databasedate;
       if(difference<86400000){
           int chatday= Integer.parseInt(date.substring(0,2));
           SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
           if(today_day==chatday) {
               assert d != null;
               return context.getResources().getString(R.string.today) + " " + sdf.format(d);
           }
           else if((today_day-chatday)==1) {
               assert d != null;
               return context.getResources().getString(R.string.yesterday) + " " + sdf.format(d);
           }
       }
       else if(difference<172800000){
           int chatday= Integer.parseInt(date.substring(0,2));
           SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
           if((today_day-chatday)==1) {
               assert d != null;
               return context.getResources().getString(R.string.yesterday) + " " + sdf.format(d);
           }
       }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yyyy HH:mm", Locale.getDefault());

       if(d!=null)
       return sdf.format(d);
       else
           return "";
    }

    public String getfileduration(Uri uri) {
        try {

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        final int file_duration = Integer.parseInt(durationStr);

        long second = (file_duration / 1000) % 60;
        long minute = (file_duration / (1000 * 60)) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
    }
         catch (Exception ignored){

        }
        return null;
    }


}
