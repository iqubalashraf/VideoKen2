package iqubal.ashraf.videoken;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iqubal.ashraf.videoken.adapter.AdapterNotes;
import iqubal.ashraf.videoken.data_model.DataModel;
import iqubal.ashraf.videoken.sql_database.DatabaseHandler;

/**
 * Created by ashrafiqubal on 28/04/17.
 */

public class YouTubePlayerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener , View.OnClickListener {
    private final String TAG = "YouTubePlayerActivity";
    private final String YOUTUBE_ID_FOR_INTENT = "iqubal.ashraf.videoken.YOUTUBE_ID";
    private final int WRITE_STORAGE_REQUEST_CODE = 112233;
    private final int RECORD_AUDIO_REQUEST_CODE = 112244;
    private final int TEXT_NOTE = 0;
    private final int VOICE_NOTE = 1;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/.ViedoKensound";
    DatabaseHandler db;
    // YouTube player view
    private YouTubePlayerView youTubeView;
    TextView addVoiceNote,addTextNote;
    public static YouTubePlayer mPlayer;
    private String YOUTUBE_VIDEO_CODE = "5u4G23_OohI";
    private static final int RECOVERY_DIALOG_REQUEST = 11211;
    protected static final int RESULT_SPEECH = 11311;
    private boolean isControlEnable = false;
    Date recording_start_time,recording_end_time;
    int timeDuration;
    private List<DataModel> dataModels = new ArrayList<DataModel>();
    private static RecyclerView notes;
    private RecyclerView.LayoutManager mLayoutManager;
    private static AdapterNotes mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_player_activity);
        Intent intent = getIntent();
        YOUTUBE_VIDEO_CODE = intent.getStringExtra(YOUTUBE_ID_FOR_INTENT);
        Log.d(TAG,YOUTUBE_VIDEO_CODE);
        db = new DatabaseHandler(this);
        dataModels=db.getAllNotes(YOUTUBE_VIDEO_CODE);
        if(dataModels.size()>0){
            Log.d(TAG,"checking "+dataModels.get(0).getSTART_TIME());
        }
        initializeAllViews();
        initializeOnClickListener();
    }
    private void initializeAllViews(){
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        // Initializing video player with developer key
        youTubeView.initialize(getString(R.string.DEVELOPER_KEY), this);
        addVoiceNote = (TextView)findViewById(R.id.addVoiceNote);
        addTextNote = (TextView)findViewById(R.id.addTextNote);
        notes = (RecyclerView)findViewById(R.id.notes);
        mLayoutManager = new LinearLayoutManager(this);
        notes.setLayoutManager(mLayoutManager);
        callAdapter();
    }
    private void initializeOnClickListener(){
        addVoiceNote.setOnClickListener(this);
        addTextNote.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addVoiceNote:
                if(askRecordAudioPermission()){
                    if(askWriteExternalStorage()){
                        startRecording();
                    }
                }
                break;
            case R.id.addTextNote:
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    if(mPlayer.isPlaying()){
                        mPlayer.pause();
                    }
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                    if(!mPlayer.isPlaying()){
                        mPlayer.play();
                    }
                }
                break;
        }
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        mPlayer = player;
        playViedo(mPlayer,YOUTUBE_VIDEO_CODE,wasRestored);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, getString(R.string.text_unable_to_initialize_youtube_player), Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            //Do some stuff
            mPlayer.setFullscreen(false);
        }else {
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RECOVERY_DIALOG_REQUEST:
                getYouTubePlayerProvider().initialize(getString(R.string.DEVELOPER_KEY), this);
                break;
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(getApplicationContext(),text.get(0),Toast.LENGTH_LONG).show();
                    timeDuration = mPlayer.getCurrentTimeMillis();
                    addNote("null",String.valueOf(timeDuration),"null",text.get(0),TEXT_NOTE);
                    callAdapter();
                }
                if(!mPlayer.isPlaying()){
                    mPlayer.play();
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode){
            case RECORD_AUDIO_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    if(askWriteExternalStorage()){
                        startRecording();
                    }
                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(),"This app needs RECORD Audio Permission to add Notes",Toast.LENGTH_SHORT).show();
                }
                return;
            case WRITE_STORAGE_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    startRecording();
                } else {
                    // permission denied
                    Toast.makeText(getApplicationContext(),"This app needs Write Storage Permission to add Notes",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }
    private void playViedo(YouTubePlayer player,String link,boolean wasRestored){
        if (!wasRestored) {
            player.loadVideo(link);
//            player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        }
    }
    private void recordAudio(final String fileName, final String start_time) {
        final MediaRecorder recorder = new MediaRecorder();
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        File file = new File(path);
//        file.mkdirs();
        if(!file.exists()){
            file.mkdirs();
        }
        recorder.setOutputFile(path+ "/"+fileName);
        try {
            recorder.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Recording...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mProgressDialog.dismiss();
                recorder.stop();
                recorder.release();
                recording_end_time = new Date();
                String duration = String.valueOf(recording_end_time.getTime()-recording_start_time.getTime());
                Log.d("RecordAudio: ",duration+" ");
                if(!mPlayer.isPlaying()){
                    mPlayer.play();
                    addNote(fileName,start_time,duration,"null",VOICE_NOTE);
                    callAdapter();
                }
            }
        });

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface p1) {
                recorder.stop();
                recorder.release();
                recording_end_time = new Date();
                String duration = String.valueOf(recording_end_time.getTime()-recording_start_time.getTime());
                if(!mPlayer.isPlaying()){
                    mPlayer.play();
                    addNote(fileName,start_time,duration,"null",VOICE_NOTE);
                    callAdapter();
                }
            }
        });
        recorder.start();
        mProgressDialog.show();
    }
    private void startRecording(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
        }
        timeDuration = mPlayer.getCurrentTimeMillis();
        Date now = new Date();
        recording_start_time = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String temp = YOUTUBE_VIDEO_CODE+"_"+now.toString()+".mp3";
        temp=temp.replace(" ","-");
        recordAudio(temp,String.valueOf(timeDuration));
    }
    private void addNote(String voice_clip_name,String start_time,String duration,String voice_in_string,int type_of_note){
        db.addNoteDetailes(YOUTUBE_VIDEO_CODE,voice_clip_name,start_time,duration,voice_in_string,type_of_note);
    }
    private boolean askRecordAudioPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_REQUEST_CODE);
            return false;
        } else{
            return true;
        }
    }
    private boolean askWriteExternalStorage(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_REQUEST_CODE);
            return false;
        } else{
            return true;
        }
    }
    private void callAdapter(){
        dataModels.clear();
        dataModels=db.getAllNotes(YOUTUBE_VIDEO_CODE);
        mAdapter = new AdapterNotes(getApplicationContext(),dataModels,mPlayer);
        notes.setAdapter(mAdapter);
    }
    public static void stopYouTubeVideo(){
        mPlayer.pause();
    }
    public static void startYouTubeVideo(){
        mPlayer.play();
    }
}
