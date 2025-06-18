package ru.crmkurgan.main.Item;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ratingview.RatingView;
import ru.crmkurgan.main.Activity.PropertyDetailActivity;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.DatabaseHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridItem extends RecyclerView.Adapter<GridItem.ItemRowHolder> {

    private final ArrayList<PropertyModels> dataList;
    private final Context mContext;
    private final DatabaseHelper databaseHelper;

    public GridItem(Context context, ArrayList<PropertyModels> dataList, int rowLayout) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final PropertyModels singleItem = dataList.get(position);
        Spanned html = Html.fromHtml(singleItem.getName());
        holder.text.setText(html);
        holder.price.setText(singleItem.getPrice());
        holder.address.setText(Html.fromHtml(singleItem.getAddress()));
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRateAvg()));
        Picasso.get()
                .load(singleItem.getImage()).fit().centerCrop()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.images);

        if (databaseHelper.getFavouriteById(singleItem.getPropid(),singleItem.getTabl())) {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.red));
        } else {
            holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
        }

        holder.favourite.setOnClickListener(view -> {
            ContentValues fav = new ContentValues();
            if (databaseHelper.getFavouriteById(singleItem.getPropid(),singleItem.getTabl())) {
                databaseHelper.removeFavouriteById(singleItem.getPropid(),singleItem.getTabl());
                holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.gray));
                Toast.makeText(mContext, mContext.getResources().getString(R.string.remove), Toast.LENGTH_SHORT).show();
            } else {
                fav.put(DatabaseHelper.KEY_ID, singleItem.getPropid());
                fav.put(DatabaseHelper.KEY_TITLE, singleItem.getName());
                fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getImage());
                fav.put(DatabaseHelper.KEY_RATE, singleItem.getRateAvg());
                fav.put(DatabaseHelper.KEY_ADDRESS, singleItem.getAddress());
                fav.put(DatabaseHelper.KEY_PRICE, singleItem.getPrice());
                fav.put(DatabaseHelper.KEY_TABL, singleItem.getTabl());
                if(singleItem.getTabl().equals("1")) {
                    fav.put(DatabaseHelper.KEY_KOMNATNOST, singleItem.getKomnatnost());
                    fav.put(DatabaseHelper.KEY_PL_OB, singleItem.getPl_ob());
                    fav.put(DatabaseHelper.KEY_PLANIR, singleItem.getPlanir());
                    fav.put(DatabaseHelper.KEY_REMONT, singleItem.getRemont());
                    fav.put(DatabaseHelper.KEY_SANUZEL, singleItem.getSanuzel());
                    fav.put(DatabaseHelper.KEY_BALKON, singleItem.getBalkon());
                    fav.put(DatabaseHelper.KEY_ETASH, singleItem.getEtash());
                    fav.put(DatabaseHelper.KEY_POSTROY, singleItem.getPostroy());
                    fav.put(DatabaseHelper.KEY_STENA, singleItem.getStena());
                }
                if(singleItem.getTabl().equals("2")) {
                    fav.put(DatabaseHelper.KEY_PL_OB, singleItem.getPl_ob());
                    fav.put(DatabaseHelper.KEY_ETASH, singleItem.getEtash());
                    fav.put(DatabaseHelper.KEY_POSTROY, singleItem.getPostroy());
                    fav.put(DatabaseHelper.KEY_STENA, singleItem.getStena());
                    fav.put(DatabaseHelper.KEY_RAZD_KOMNAT, singleItem.getRazd_komnat());
                    fav.put(DatabaseHelper.KEY_ELECTRO, singleItem.getElectro());
                    fav.put(DatabaseHelper.KEY_OTOP, singleItem.getOtop());
                    fav.put(DatabaseHelper.KEY_GAZ, singleItem.getGaz());
                    fav.put(DatabaseHelper.KEY_CANALYA, singleItem.getCanalya());
                    fav.put(DatabaseHelper.KEY_ZEMLYA, singleItem.getZemlya());
                }
                if(singleItem.getTabl().equals("3")) {
                    fav.put(DatabaseHelper.KEY_PL_OB, singleItem.getPl_ob());
                    fav.put(DatabaseHelper.KEY_ETASH, singleItem.getEtash());
                }
                databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                holder.favourite.setColorFilter(mContext.getResources().getColor(R.color.red));
                Toast.makeText(mContext, mContext.getResources().getString(R.string.addfav), Toast.LENGTH_SHORT).show();
            }
        });

        holder.lyt_parent.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, PropertyDetailActivity.class);
                intent.putExtra("Id", singleItem.getPropid());
                intent.putExtra("Tabl", singleItem.getTabl());
                mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView text, price, address;
        ImageView images, favourite;
        RatingView ratingView;
        LinearLayout lyt_parent;

        ItemRowHolder(View itemView) {
            super(itemView);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            images = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            favourite = itemView.findViewById(R.id.favourite);
            price = itemView.findViewById(R.id.price);
            address = itemView.findViewById(R.id.address);
            ratingView = itemView.findViewById(R.id.ratingView);
        }
    }
}

