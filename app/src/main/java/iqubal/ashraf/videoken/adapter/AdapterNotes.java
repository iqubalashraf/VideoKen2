package iqubal.ashraf.videoken.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import iqubal.ashraf.videoken.R;
import iqubal.ashraf.videoken.YouTubePlayerActivity;
import iqubal.ashraf.videoken.data_model.DataModel;

/**
 * Created by ashrafiqubal on 29/04/17.
 */

public class AdapterNotes  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DataModel> dataModels = new ArrayList<DataModel>();
    Context context;
    YouTubePlayer mPlayer;
    private final int TEXT_NOTE = 0;
    private final int VOICE_NOTE = 1;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/.ViedoKensound/";
    private boolean isMediaPlayerPlaying = false;
    static MediaPlayer mediaPlayer;
    private int clickedPosition = 0;
    public AdapterNotes(Context context,List<DataModel> dataModels ,YouTubePlayer mPlayer){
        this.context = context;
        this.dataModels = dataModels;
        this.mPlayer = mPlayer;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(dataModels.get(viewType).getNOTE_TYPE()==TEXT_NOTE){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_text_note, parent, false);
            return new ViewHolderTextNote(itemView);
        }else if(dataModels.get(viewType).getNOTE_TYPE()==VOICE_NOTE){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_voice_note, parent, false);
            return new ViewHolderVoiceNote(itemView);
        }
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_text_note, parent, false);
        return new ViewHolderVoiceNote(itemView);
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(dataModels.get(position).getNOTE_TYPE()==TEXT_NOTE){
            int startTime = Integer.parseInt(dataModels.get(position).getSTART_TIME());
            startTime = startTime/1000;
            int startTimeMin = 0;
            String startTimeMinString="00",startTimeSec="00";
            if(startTime>60){
                startTimeMin = startTime/60;
                startTime = startTimeMin%60;
            }
            if(startTimeMin<10){
                startTimeMinString = "0"+startTimeMin;
            }
            if(startTime<10){
                startTimeSec = "0"+startTime;
            }else if(startTime>10){
                startTimeSec = startTime+"";
            }
            try {
                ((ViewHolderTextNote) holder).textViewNoteTime.setText(startTimeMinString+":"+startTimeSec);
                ((ViewHolderTextNote) holder).textViewNote.setText(dataModels.get(position).getVOICE_IN_STRING());
            }catch (Exception e){
                Log.d("AdapterNotes","Error: "+e.getMessage());
            }

        }else if(dataModels.get(position).getNOTE_TYPE()==VOICE_NOTE){
            int startTime = Integer.parseInt(dataModels.get(position).getSTART_TIME());
            startTime = startTime/1000;
            int startTimeMin = 0;
            String startTimeMinString="00",startTimeSec="00";
            if(startTime>60){
                startTimeMin = startTime/60;
                startTime = startTimeMin%60;
            }
            if(startTimeMin<10){
                startTimeMinString = "0"+startTimeMin;
            }
            if(startTime<10){
                startTimeSec = "0"+startTime;
            }else if(startTime>10){
                startTimeSec = startTime+"";
            }
            if(!(position == clickedPosition)){
                ((ViewHolderVoiceNote) holder).buttonPlayPause.setText("Play");
            }

            ((ViewHolderVoiceNote) holder).textViewNoteTime.setText(startTimeMinString+":"+startTimeSec);
            ((ViewHolderVoiceNote) holder).textViewDuration.setText(String.valueOf(Integer.parseInt(dataModels.get(position).getDURATION())/1000)+" sec\'s");
            ((ViewHolderVoiceNote) holder).buttonPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPosition = position;
                    notifyDataSetChanged();
                    if(isMediaPlayerPlaying){
                        mediaPlayer.stop();
                        if(!YouTubePlayerActivity.mPlayer.isPlaying()){
                            YouTubePlayerActivity.startYouTubeVideo();
                        }
                        isMediaPlayerPlaying = false;
                        ((ViewHolderVoiceNote) holder).buttonPlayPause.setText("Play");
                    }else {
                        playMedia(position);
                        if(YouTubePlayerActivity.mPlayer.isPlaying()){
                            YouTubePlayerActivity.stopYouTubeVideo();
                        }
                        ((ViewHolderVoiceNote) holder).buttonPlayPause.setText("Pause");

                    }

                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return dataModels.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    private class ViewHolderTextNote extends RecyclerView.ViewHolder {
        private TextView textViewNoteTime, textViewNote;

        private ViewHolderTextNote(View itemView) {
            super(itemView);
            textViewNoteTime = (TextView) itemView.findViewById(R.id.textViewNoteTime);
            textViewNote = (TextView) itemView.findViewById(R.id.textViewNote);
        }
    }
    private class ViewHolderVoiceNote extends RecyclerView.ViewHolder {
        private TextView textViewNoteTime, textViewDuration,buttonPlayPause;

        private ViewHolderVoiceNote(View itemView) {
            super(itemView);
            textViewNoteTime = (TextView) itemView.findViewById(R.id.textViewNoteTime);
            textViewDuration = (TextView) itemView.findViewById(R.id.textViewDuration);
            buttonPlayPause = (TextView) itemView.findViewById(R.id.buttonPlayPause);
        }
    }
    private void playMedia(int position){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path+dataModels.get(position).getVOICE_CLIP_NAME());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    clickedPosition = 999999;
                    notifyDataSetChanged();
                    mediaPlayer.stop();
                    if(!YouTubePlayerActivity.mPlayer.isPlaying()){
                        YouTubePlayerActivity.startYouTubeVideo();
                    }
                    isMediaPlayerPlaying = false;
                }
            });
            mediaPlayer.start();
            isMediaPlayerPlaying = true;
        }catch (Exception e){
            Log.d("Error",e.getMessage());
        }
    }
}
