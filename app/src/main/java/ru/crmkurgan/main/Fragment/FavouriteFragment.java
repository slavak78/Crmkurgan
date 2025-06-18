package ru.crmkurgan.main.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.crmkurgan.main.Item.ListItem;
import ru.crmkurgan.main.Models.PropertyModels;
import ru.crmkurgan.main.R;
import ru.crmkurgan.main.Utils.DatabaseHelper;

import java.util.ArrayList;


public class FavouriteFragment extends Fragment {


    View getView;
    Context context;
    ArrayList<PropertyModels> listItem;
    public RecyclerView recyclerView;
    ListItem adapter;
    DatabaseHelper databaseHelper;
    RelativeLayout notFound, progress;
    CardView filterandsort;
    Toolbar toolbar;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_recycle, container, false);
        context = getContext();
        toolbar = getView.findViewById(R.id.toolbar);
        listItem = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());
        recyclerView = getView.findViewById(R.id.recycle);
        filterandsort = getView.findViewById(R.id.rlfilter);
        notFound = getView.findViewById(R.id.notfound);
        progress = getView.findViewById(R.id.progress);
        toolbar.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        filterandsort.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));



        return getView;
    }

    @Override
    public void onResume() {
        super.onResume();
            listItem = databaseHelper.getFavourite();
        displayData();
    }

    private void displayData() {
        adapter = new ListItem(getActivity(), listItem, R.layout.item_list);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            notFound.setVisibility(View.VISIBLE);
        } else {
            notFound.setVisibility(View.GONE);
        }

    }

}
