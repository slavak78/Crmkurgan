package ru.crmkurgan.main.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import downloader.Error;
import downloader.OnDownloadListener;
import downloader.PRDownloader;
import downloader.request.DownloadRequest;
import ru.crmkurgan.main.BuildConfig;
import ru.crmkurgan.main.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullImageFragment extends Fragment {

    View getView;
    Context context;
    ImageButton sharebtn;
    Button savebtn, savebtn2;

    ImageView singleImage, closeGallery;
    String imageUrl,chat_id;
    ProgressBar progressBar;

    ProgressDialog progressDialog;
    DownloadRequest prDownloader;

    File direct;
    File fullpath;
    int width,height;
    ActivityResultLauncher<String> mPermissionResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getView = inflater.inflate(R.layout.fragment_fullimage, container, false);
        context = getContext();

        mPermissionResult = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(result) {
                        Toast.makeText(context, getResources().getString(R.string.clicks1), Toast.LENGTH_LONG).show();
                    }
                });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         height = displayMetrics.heightPixels;
         width = displayMetrics.widthPixels;
        assert getArguments() != null;
        imageUrl = getArguments().getString("image_url");
        chat_id = getArguments().getString("chat_id");
        savebtn2 = getView.findViewById(R.id.savebtn2);

        closeGallery = getView.findViewById(R.id.close_gallery);
        closeGallery.setOnClickListener(v -> requireActivity().onBackPressed());

        progressDialog = new ProgressDialog(context,R.style.AlertDialog);
        progressDialog.setMessage(getResources().getString(R.string.wait));

        PRDownloader.initialize(requireActivity().getApplicationContext());

        fullpath = new File(Environment.getExternalStorageDirectory() +"/Crmkurgan/"+chat_id+".jpg");

        savebtn= getView.findViewById(R.id.savebtn);
        if(fullpath.exists()){
            savebtn.setVisibility(View.GONE);
            savebtn2.setVisibility(View.VISIBLE);
        }

        direct = new File(Environment.getExternalStorageDirectory() +"/Crmkurgan/");

        prDownloader = PRDownloader.download(imageUrl, direct.getPath(), chat_id+".jpg")
                .build()
                .setOnStartOrResumeListener(() -> {

                })
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {

                })
                .setOnProgressListener(progress -> {

                });


        savebtn.setOnClickListener(v -> Savepicture(false));
        progressBar = getView.findViewById(R.id.progress);
        singleImage = getView.findViewById(R.id.single_image);

        if(fullpath.exists()){
            Uri uri= Uri.parse(fullpath.getAbsolutePath());
            singleImage.setImageURI(uri);
        }else {
            progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).placeholder(R.drawable.image_placeholder)
                    .into(singleImage, new Callback() {
                        @Override
                        public void onSuccess() {

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }

        sharebtn= getView.findViewById(R.id.sharebtn);
        sharebtn.setOnClickListener(v -> SharePicture());


        return getView;
    }

    public void SharePicture(){
        if(Checkstoragepermision()) {
            Uri bitmapuri;
            if(fullpath.exists()){
                bitmapuri= Uri.parse(fullpath.getAbsolutePath());
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, bitmapuri);
                startActivity(Intent.createChooser(intent, ""));
            }
            else {
                Savepicture(true);
            }

        }
    }

    public void Savepicture(final boolean isfromshare){
        if(Checkstoragepermision()) {

            final File direct = new File(Environment.getExternalStorageDirectory() + "/DCIM/Crmkurgan/");
            progressDialog.show();
            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.parse(direct.getPath() + chat_id + ".jpg"));
                    context.sendBroadcast(intent);
                    progressDialog.dismiss();
                    if (isfromshare) {
                        SharePicture();
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialog)
                                .setTitle(getResources().getString(R.string.saved))
                                .setMessage(fullpath.getAbsolutePath())
                                .setNegativeButton("ok", (dialogInterface, i) -> {
                                });
                                alertDialog.show();
                    }
                }

                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();
                    Toast.makeText(context, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();

                }


            });
        }
    }

    public boolean Checkstoragepermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {
                mPermissionResult.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }else {

            return true;
        }
    }


}


