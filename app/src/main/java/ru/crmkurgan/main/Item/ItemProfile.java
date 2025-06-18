package ru.crmkurgan.main.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ru.crmkurgan.main.R;
import com.squareup.picasso.Picasso;
import dragrecyclerview.DragAdapter;
import dragrecyclerview.DragHolder;
import dragrecyclerview.DragRecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemProfile extends DragAdapter {
    Context context;

    ArrayList<String> photos;

    private final ItemProfile.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item, int postion, View view);
    }


    public ItemProfile(Context context, ArrayList<String> arrayList, ItemProfile.OnItemClickListener listener)  {
        super(context,arrayList);
        this.context=context;
        photos=arrayList;
        this.listener=listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {

        return new HistoryviewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_editprofile, viewGroup, false));


    }



    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final DragRecyclerView.ViewHolder hol, final int position) {
        super.onBindViewHolder(hol, position);
        HistoryviewHolder holder = (HistoryviewHolder) hol;
        holder.bind(photos.get(position),position,listener);

        if(photos.get(position).equals("")){
            holder.cancelButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add));
            Picasso.get().load("null").placeholder(R.drawable.image_placeholder).centerCrop().resize(200,300).into(holder.image);

        }else {
            holder.cancelButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cancel));
            Picasso.get().load(photos.get(position)).placeholder(R.drawable.image_placeholder).centerCrop().resize(200,300).into(holder.image);
        }
    }

    static class HistoryviewHolder extends DragHolder {
        View getView;
        CircleImageView image;
        ImageButton cancelButton;
        public HistoryviewHolder(View itemView) {
            super(itemView);
            getView = itemView;
            image = getView.findViewById(R.id.image);
            cancelButton = getView.findViewById(R.id.button);
        }


        public void bind(final String item, final int position , final ItemProfile.OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item,position,v));

            cancelButton.setOnClickListener(v -> listener.onItemClick(item,position,v));
        }


    }

}

