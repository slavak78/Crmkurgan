package ru.crmkurgan.main.Fragment;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.crmkurgan.main.Constants.BaseApp;
import ru.crmkurgan.main.Item.MessageItem;
import ru.crmkurgan.main.Constants.Functions;
import ru.crmkurgan.main.Activity.MainActivity;
import ru.crmkurgan.main.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import ru.crmkurgan.main.Models.MessageModels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class MessageFragment extends Fragment {


    View getView;
    Context context;
    ActivityResultLauncher<String> mPermissionResult;
    RecyclerView inboxList;
    BaseApp baseApp;

    ArrayList<MessageModels> inboxArraylist;

    DatabaseReference rootRef;

    MessageItem inboxItem;
    MessageModels inn;

    boolean isviewCreated = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_message, container, false);
        context = getContext();

        rootRef = FirebaseDatabase.getInstance().getReference();
        baseApp = BaseApp.getInstance();

        inboxList = getView.findViewById(R.id.inboxlist);

        inboxArraylist =new ArrayList<>();

        inboxList = getView.findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inboxList.setLayoutManager(layout);
        inboxList.setHasFixedSize(false);
        inboxItem = new MessageItem(context, inboxArraylist, item -> {
            inn = item;
            mPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }, item -> {

        });

        inboxList.setAdapter(inboxItem);



        getView.setOnClickListener(v -> Functions.hideSoftKeyboard(requireActivity()));

        isviewCreated = true;


        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> chatFragment(MainActivity.user_id,inn.getId(),inn.getName(),inn.getPicture()));

        return getView;
    }

    ValueEventListener valueEventListener;

    Query inboxQuery;

    @Override
    public void onStart() {
        super.onStart();
        if (baseApp.getIsLogin()) {
            inboxQuery = rootRef.child("Inbox").child(MainActivity.user_id).orderByChild("date");
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    inboxArraylist.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        MessageModels model = new MessageModels();
                        model.setId(ds.getKey());
                        model.setName(Objects.requireNonNull(ds.child("name").getValue()).toString());
                        model.setMessage(Objects.requireNonNull(ds.child("msg").getValue()).toString());
                        model.setTimestamp(Objects.requireNonNull(ds.child("date").getValue()).toString());
                        model.setStatus(Objects.requireNonNull(ds.child("status").getValue()).toString());
                        model.setPicture(Objects.requireNonNull(ds.child("pic").getValue()).toString());
                        inboxArraylist.add(model);
                    }
                    Collections.reverse(inboxArraylist);
                    inboxItem.notifyDataSetChanged();

                    if (inboxArraylist.isEmpty()) {
                        getView.findViewById(R.id.nomatch).setVisibility(View.VISIBLE);
                    } else {
                        getView.findViewById(R.id.nomatch).setVisibility(View.GONE);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            };


            inboxQuery.addValueEventListener(valueEventListener);
        } else {
            getView.findViewById(R.id.nomatch).setVisibility(View.VISIBLE);
        }

    }



    @Override
    public void onStop() {
        super.onStop();
        if(inboxQuery !=null)
            inboxQuery.removeEventListener(valueEventListener);
    }

    public void chatFragment(String senderid,String receiverid,String name,String picture){
        ChatFragment chat_fragment = new ChatFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        chat_fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainFragment, chat_fragment).commit();
    }
}
