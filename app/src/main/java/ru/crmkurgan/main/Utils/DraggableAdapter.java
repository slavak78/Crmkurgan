package ru.crmkurgan.main.Utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.crmkurgan.main.Activity.AddPropertyActivity;
import ru.crmkurgan.main.R;

public class DraggableAdapter
        extends RecyclerView.Adapter<DraggableAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableAdapter.MyViewHolder> {


    private final AbstractDataProvider mProvider;
    private final Context mContext;
    private int mItemMoveMode = RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT;
    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public ImageView mImageView;
        public ImageView mDelPhoto;

        public MyViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mImageView = v.findViewById(R.id.imagepart);
            mDelPhoto = v.findViewById(R.id.delphoto);
        }
    }


        public DraggableAdapter(AbstractDataProvider dataProvider, Context context) {
            mProvider = dataProvider;
            mContext = context;
            setHasStableIds(true);
        }

    public void setItemMoveMode(int itemMoveMode) {
        mItemMoveMode = itemMoveMode;
    }
        @Override
        public long getItemId(int position) {
            return mProvider.getItem(position).getId();
        }

        @Override
        public int getItemViewType(int position) {
            return mProvider.getItem(position).getViewType();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View v = inflater.inflate(R.layout.cellgrid, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final AbstractDataProvider.Data item = mProvider.getItem(position);
            Picasso.get().load(item.getUri()).fit().centerCrop().into(holder.mImageView);
            holder.mDelPhoto.setOnClickListener(v -> ((AddPropertyActivity) mContext).delitem(position));
        }

        @Override
        public int getItemCount() {
            return mProvider.getCount();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
        if(mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
            mProvider.moveItem(fromPosition, toPosition);
        } else {
            mProvider.swapItem(fromPosition, toPosition);
        }
        }

        @Override
        public boolean onCheckCanStartDrag(@NonNull MyViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(@NonNull MyViewHolder holder, int position) {
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }

        @Override
        public void onItemDragStarted(int position) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            notifyDataSetChanged();
        }


    }
