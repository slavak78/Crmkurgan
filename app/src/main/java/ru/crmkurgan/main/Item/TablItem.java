package ru.crmkurgan.main.Item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import ru.crmkurgan.main.Activity.AllPropByTablActivity;
import ru.crmkurgan.main.Models.TablModels;
import ru.crmkurgan.main.R;

public class TablItem extends RecyclerView.Adapter<TablItem.ItemRowHolder> {

    private final ArrayList<TablModels> dataList;
    private final Context mContext;
    private final int rowLayout;

    public TablItem(Context context, ArrayList<TablModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        this.rowLayout = rowLayout;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final TablModels singleItem = dataList.get(position);
        holder.text.setText(singleItem.getTablName());
        Random rand = new Random();
        int i = rand.nextInt(4) + 1;


        switch (i) {
            case 1:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_primary));
                break;
            case 2:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_blue));
                break;

            case 3:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_green));
                break;

            case 4:
                holder.background.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_yellow));
                break;

            default:
                break;
        }

        holder.background.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AllPropByTablActivity.class);
            intent.putExtra("Id", singleItem.getTablId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView images, background;

        ItemRowHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            images = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
    }
}
