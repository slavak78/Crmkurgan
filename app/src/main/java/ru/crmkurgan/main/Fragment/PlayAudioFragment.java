package ru.crmkurgan.main.Fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import ru.crmkurgan.main.R;
import nl.changer.audiowife.AudioWife;


public class PlayAudioFragment extends Fragment {

    View getView;
    Context context;
    ImageButton playBtn, pauseBtn;
    SeekBar seekBar;
    TextView durationTime, totalTime;

    AudioWife audioWife;


    ImageView close_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getView = inflater.inflate(R.layout.fragment_playaudio, container, false);

        context = getContext();

        close_btn= getView.findViewById(R.id.close_btn);
        playBtn = getView.findViewById(R.id.playbtn);
        pauseBtn = getView.findViewById(R.id.pause_btn);
        seekBar= getView.findViewById(R.id.seekbar);
        durationTime = getView.findViewById(R.id.duration_time);
        totalTime = getView.findViewById(R.id.totaltime);

        assert getArguments() != null;
        String filepath=getArguments().getString("path");
        Uri uri= Uri.parse(filepath);

        audioWife=AudioWife.getInstance();
        audioWife.init(context, uri)
                .setPlayView(playBtn)
                .setPauseView(pauseBtn)
                .setSeekBar(seekBar)
                .setRuntimeView(durationTime)
                .setTotalTimeView(totalTime);

        audioWife.play();

        close_btn.setOnClickListener(v -> requireActivity().onBackPressed());

        return getView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        audioWife.pause();
        audioWife.release();
    }
}
