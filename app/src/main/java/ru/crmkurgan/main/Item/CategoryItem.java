package ru.crmkurgan.main.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import ru.crmkurgan.main.Models.CategoryModels;
import ru.crmkurgan.main.R;
import java.util.ArrayList;

public class CategoryItem extends RecyclerView.Adapter<CategoryItem.ItemRowHolder> {

    private final ArrayList<CategoryModels> dataList;
    private final int rowLayout;

    public CategoryItem(Context context, ArrayList<CategoryModels> dataList, int rowLayout) {
        this.dataList = dataList;
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
        final CategoryModels singleItem = dataList.get(position);
        String mumu = singleItem.getCategoryName();
        holder.check.setText(singleItem.getCategoryName());
        holder.check.setChecked(true);
        holder.check.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                singleItem.setCategorySelected(1);
            } else {
                singleItem.setCategorySelected(0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        AppCompatCheckBox check;

        ItemRowHolder(View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.cat);
        }
    }
}
