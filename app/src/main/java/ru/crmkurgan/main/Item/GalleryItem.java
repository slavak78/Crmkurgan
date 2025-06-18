package ru.crmkurgan.main.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import ru.crmkurgan.main.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryItem extends RecyclerView.Adapter<GalleryItem.ItemRowHolder> {

    private final ArrayList<String> dataList;
    private OnItemClickListener onItemClickListener;

    public GalleryItem(Context context, ArrayList<String> dataList, int rowLayout) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_square, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        holder.text.setVisibility(View.GONE);
        final String p = dataList.get(position);

        Picasso.get()
                    .load(dataList.get(position)).fit().centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.images);

        holder.images.setOnClickListener(v -> onItemClickListener.onItemClick(v, p, position));
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text, nodata;
        ImageView images;
        CardView cardView;

        ItemRowHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            nodata = itemView.findViewById(R.id.nodata);
            images = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
        }
    }
    

    public interface OnItemClickListener {
        void onItemClick(View view, String viewModel, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
